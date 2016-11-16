package org.atilf.module.tools;

import org.atilf.models.termith.TermithIndex;
import org.atilf.models.disambiguation.EvaluationProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.Map;

import static org.atilf.models.disambiguation.ContextResources.*;

/**
 * Write the result of disambiguation in tei file format
 * @author Simon Meoni
 *         Created on 25/10/16.
 */
public class DisambiguationTeiWriter implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeiWriter.class.getName());
    private final String _p;
    private final Map<String, EvaluationProfile> _evaluationLexicon;
    private DocumentBuilder _dBuilder;
    private Document _doc;
    private DocumentBuilderFactory _dbFactory = DocumentBuilderFactory.newInstance();
    private XPath _xpath = XPathFactory.newInstance().newXPath();

    /**
     * constructor for DisambiguationTeiWriter
     * @param p the file name
     * @param evaluationLexicon the evaluation lexicon that contains the result for disambiguation for one file
     */
    public DisambiguationTeiWriter(String p, Map<String, EvaluationProfile> evaluationLexicon) {
        /*
        prepare dom parser
         */
        _p = p;
        _evaluationLexicon = evaluationLexicon;

        _xpath.setNamespaceContext(NAMESPACE_CONTEXT);

        try {
            _dbFactory.setNamespaceAware(true);
            _dBuilder = _dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOGGER.error("error during the creation of documentBuilder object : ", e);
        }
        try {
            _doc = _dBuilder.parse(p);
        } catch (SAXException | IOException e) {
            LOGGER.error("error during the parsing of document",e);
        }
    }

    public void execute() {
        XPathExpression span;
        XPathExpression ana;
        XPathExpression corresp;
        try {
            /*
            compile needed xpath expression
             */
            span = _xpath.compile(SPAN);
            ana = _xpath.compile(ANA);
            corresp = _xpath.compile(CORRESP);
            /*
            get all the span of corresp element
             */
            NodeList termNodes = (NodeList) span.evaluate(_doc, XPathConstants.NODESET);
            for (int i = 0; i < termNodes.getLength(); i++){
                /*
                get corresp attribute of span element
                 */
                Node correspVal = (Node) corresp.evaluate(termNodes.item(i), XPathConstants.NODE);
                /*
                get ana attribute of span element
                 */
                Node anaVal = (Node) ana.evaluate(termNodes.item(i), XPathConstants.NODE);

                /*
                write result of disambiguation in ana attribute
                 */
                String termId = correspVal.getNodeValue().substring(1) + "_" + anaVal.getNodeValue().substring(1);
                if (_evaluationLexicon.containsKey(termId)) {
                    anaVal.setNodeValue(
                            anaVal.getNodeValue() + " #" + _evaluationLexicon.get(termId).getDisambiguationId()
                    );
                }
            }

            try {
                /*
                write result
                 */
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
                _doc.setXmlStandalone(true);
                DOMSource source = new DOMSource(_doc);
                StreamResult result = new StreamResult(TermithIndex.getOutputPath() + "/"
                        + FilesUtils.nameNormalizer(_p) + ".xml");
                transformer.transform(source, result);
            } catch (TransformerException e) {
                LOGGER.error("error during file writing",e);
            }

        } catch (XPathExpressionException e) {
            LOGGER.error("error during the parsing of document",e);
        }
    }

    /**
     * call execute method
     */
    @Override
    public void run() {
        LOGGER.debug("write tei disambiguation for :" + _p);
        execute();
        LOGGER.debug("tei disambiguation is written for :" + _p);
    }

}
