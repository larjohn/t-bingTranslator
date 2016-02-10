package eu.openbudgets.unifiedviews;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.dataset.DatasetBuilder;
import eu.unifiedviews.helpers.dataunit.rdf.RDFHelper;
import eu.unifiedviews.helpers.dataunit.rdf.RdfDataUnitUtils;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.helpers.dpu.extension.rdf.simple.WritableSimpleRdf;
import org.apache.commons.lang3.StringUtils;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.*;
import org.openrdf.query.parser.ParsedBooleanQuery;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.query.parser.QueryParserUtil;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Main data processing unit class.
 *
 * @author Unknown
 */
@DPU.AsTransformer
public class BingTranslator extends AbstractDpu<BingTranslatorConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(BingTranslator.class);

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    @DataUnit.AsInput(name = "rdfInput")
    public RDFDataUnit input;

    public static final String SUPPORTED_PROTOCOLS = "dpu.uv-t-bingTranslator.allowed.protocols";
/*    @RdfConfiguration.ContainsConfiguration
    @DataUnit.AsInput(name = "config", optional = true)
    public RDFDataUnit rdfConfiguration;*/


    @DataUnit.AsOutput(name = "rdfOutput")
    public WritableRDFDataUnit output;

    @ExtensionInitializer.Init(param = "output")
    public WritableSimpleRdf rdfTableWrap;

    public BingTranslator() {
        super(BingTranslatorVaadinDialog.class, ConfigHistory.noHistory(BingTranslatorConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {

        Translate.setClientId(config.getBingClientId());

        Translate.setClientSecret(config.getBingClientSecret());
//<http://www.w3.org/2004/02/skos/core#prefLabel>
        String sparql_query = "SELECT distinct ?term ?predicate ?label {?term ?predicate ?label. VALUES ?predicate {";
        for(LabelEntry predicate: config.getLabelPredicates()){
            sparql_query += " <"+ predicate.getLabelURI()+"> ";
        }
        sparql_query+="} }LIMIT 5";
        ContextUtils.sendMessage(ctx, DPUContext.MessageType.INFO, "status.starting", "");
        if (StringUtils.isBlank(sparql_query)) {
            throw ContextUtils.dpuException(ctx, "error.invalidConfiguration.queryEmpty");
        }
        ParsedQuery parsedQuery;
        try {
            parsedQuery = QueryParserUtil.parseQuery(QueryLanguage.SPARQL, sparql_query, null);
            if (!(parsedQuery instanceof ParsedTupleQuery) && !(parsedQuery instanceof ParsedBooleanQuery)) {
                throw ContextUtils.dpuException(ctx, "error.unsupported.query.type");
            }
        } catch (UnsupportedQueryLanguageException | MalformedQueryException ex) {
            throw ContextUtils.dpuException(ctx, ex, "error.query.parse");
        }

        final RDFDataUnit.Entry entryOutput = this.faultTolerance.execute(new FaultTolerance.ActionReturn<RDFDataUnit.Entry>() {

            @Override
            public RDFDataUnit.Entry action() throws Exception {
                return RdfDataUnitUtils.addGraph(output, config.getOutputGraphName());
            }
        });

        this.rdfTableWrap.setOutput(entryOutput);


        // Get input graphs.
        LOG.info("Reading input graphs ...");
        ContextUtils.sendShortInfo(ctx, "BingTranslator.message");
        final Set<RDFDataUnit.Entry> graphs;
        try {
            graphs = RDFHelper.getGraphs(input);
        } catch (DataUnitException ex) {
            throw ContextUtils.dpuException(ctx, "error.dataunit.graphList");
        }

        RepositoryConnection connection = null;
        try {
            connection = input.getConnection();
            Set<URI> graphURIs = new HashSet<>();
            for (RDFDataUnit.Entry entry : graphs) {
                graphURIs.add(entry.getDataGraphURI());
            }

            // Prepare query.
            if (parsedQuery instanceof ParsedTupleQuery) {
                TupleQuery query;
                TupleQueryResult result = null;
                try {
                    query = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparql_query);
                    query.setDataset(new DatasetBuilder().withDefaultGraphs(graphURIs).build());
                    result = query.evaluate();
                    if (result.hasNext()) {
                        while (result.hasNext()
                                ) {
                            // From Greek -> English
                            BindingSet triple = result.next();
                            String translatedText = Translate.execute(triple.getValue("label").stringValue().toLowerCase(), Language.fromString(config.getInputLanguage()), Language.fromString(config.getOutputLanguage()));

                            System.out.println(Language.fromString(config.getInputLanguage()).name() + "->" + Language.fromString(config.getOutputLanguage()).name()+": " + translatedText);

                            ValueFactory factory = new ValueFactoryImpl();
                            Value literal = factory.createLiteral(translatedText, config.getOutputLanguage());
                            rdfTableWrap.add(factory.createURI(triple.getValue("term").stringValue()),factory.createURI(triple.getValue("predicate").stringValue()), literal);

                        }

                    }
                } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
                    throw ContextUtils.dpuException(ctx, "error.query.execution");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (result != null) {
                        try {
                            result.close();
                        } catch (QueryEvaluationException ex) {
                            LOG.warn("Error in close", ex);
                        }
                    }
                }
            }

        } catch (DataUnitException ex) {
            throw ContextUtils.dpuException(ctx, ex, "error.dataunit");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.info("Error in close", ex);
                }
            }
        }


    }

}
