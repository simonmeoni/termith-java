package org.atilf.module.tei.morphology;

import org.atilf.module.termsuite.morphology.MorphologyParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Meoni
 *         Created on 26/08/16.
 */
public class MorphologyTokenizerTest {
    private MorphologyTokenizer _syntaxBody;
    private MorphologyTokenizer _syntaxBody2;
    private MorphologyTokenizer _basicTokenInjector;
    private MorphologyTokenizer _insideTokenInjector;
    private MorphologyTokenizer _insideTokenInjector2;
    private MorphologyTokenizer _commentTokenInjector;
    private MorphologyTokenizer _symbolTokenInjector;
    private MorphologyTokenizer _symbolTokenInjector2;
    private MorphologyTokenizer _symbolTokenInjector3;
    private MorphologyTokenizer _alignmentTokenInjector;
    private MorphologyTokenizer _alignmentTokenInjector2;
    private String _expectedStringBuilder;
    private String _expectedStringBuilder2;
    private List<String> _offsetIdAlignment;

    @Before
    public void setUp() throws Exception {

        //bodySplitter test
        _expectedStringBuilder =
                "<text>\n" +
                        "    <body>\n" +
                        "      <div>\n" +
                        "        <p>\n" +
                        "Le chien mange des chips\n" +
                        "  </text>";

        _expectedStringBuilder2 =
                "<text xml:id=\"pas fraiche du tout\">\n" +
                        "    <body>\n" +
                        "      <div>\n" +
                        "        <p>\n" +
                        "Le chien mange des chips pas fraiche\n" +
                        "  </text>";

        StringBuilder StringBuilder = new StringBuilder(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<!--Version 1.2 générée le 5-4-2016-->\n" +
                        "<TEI xmlns:tei=\"http://www.tei-c.org/ns/1.0\" " +
                        "xmlns=\"http://www.tei-c.org/ns/1.0\" xml:lang=\"en\">\n" +
                        "  <teiHeader>\n" +
                        "\t\t<fileDesc>\n" +
                        "\t\t\t<titleStmt>\n" +
                        "\t\t\t\t<title/>\n" +
                        "\t\t\t</titleStmt>\n" +
                        "\t\t\t<publicationStmt>\n" +
                        "\t\t\t\t<publisher/>\n" +
                        "\t\t\t</publicationStmt>\n" +
                        "\t\t\t<sourceDesc>\n" +
                        "\t\t\t\t<p/>\n" +
                        "\t\t\t</sourceDesc>\n" +
                        "\t\t</fileDesc>\n" +
                        "\t</teiHeader>\n" +
                        "  <text>\n" +
                        "    <body>\n" +
                        "      <div>\n" +
                        "        <p>\n" +
                        "Le chien mange des chips\n" +
                        "  </text>");

        StringBuilder StringBuilder2 = new StringBuilder(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<!--Version 1.2 générée le 5-4-2016-->\n" +
                        "<TEI xmlns:tei=\"http://www.tei-c.org/ns/1.0\" " +
                        "xmlns=\"http://www.tei-c.org/ns/1.0\" xml:lang=\"en\">\n" +
                        "  <teiHeader>\n" +
                        "\t\t<fileDesc>\n" +
                        "\t\t\t<titleStmt>\n" +
                        "\t\t\t\t<title/>\n" +
                        "\t\t\t</titleStmt>\n" +
                        "\t\t\t<publicationStmt>\n" +
                        "\t\t\t\t<publisher/>\n" +
                        "\t\t\t</publicationStmt>\n" +
                        "\t\t\t<sourceDesc>\n" +
                        "\t\t\t\t<p/>\n" +
                        "\t\t\t</sourceDesc>\n" +
                        "\t\t</fileDesc>\n" +
                        "\t</teiHeader>\n" +
                        "  <text xml:id=\"pas fraiche du tout\">\n" +
                        "    <body>\n" +
                        "      <div>\n" +
                        "        <p>\n" +
                        "Le chien mange des chips pas fraiche\n" +
                        "  </text>");

        StringBuilder StringBuilder3 = new StringBuilder(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<!--Version 1.2 générée le 5-4-2016-->\n" +
                        "<TEI xmlns:tei=\"http://www.tei-c.org/ns/1.0\" " +
                        "xmlns=\"http://www.tei-c.org/ns/1.0\" xml:lang=\"en\">\n" +
                        "  <teiHeader>\n" +
                        "\t\t<fileDesc>\n" +
                        "\t\t\t<titleStmt>\n" +
                        "\t\t\t\t<title/>\n" +
                        "\t\t\t</titleStmt>\n" +
                        "\t\t\t<publicationStmt>\n" +
                        "\t\t\t\t<publisher/>\n" +
                        "\t\t\t</publicationStmt>\n" +
                        "\t\t\t<sourceDesc>\n" +
                        "\t\t\t\t<p/>\n" +
                        "\t\t\t</sourceDesc>\n" +
                        "\t\t</fileDesc>\n" +
                        "\t</teiHeader><\n" +
                        "  <text xml:id=\"pas fraiche du tout\">\n" +
                        "    <body>\n" +
                        "      <div>\n" +
                        "        <p>\n" +
                        "Le chien mange des chips pas fraiche\n" +
                        "<!--lalalalal--></text>");

        _syntaxBody = new MorphologyTokenizer(StringBuilder);
        _syntaxBody2 = new MorphologyTokenizer(StringBuilder2);
        _syntaxBody.teiTextSplitter();
        _syntaxBody2.teiTextSplitter();

        MorphologyTokenizer syntaxBody3 = new MorphologyTokenizer(StringBuilder3);
        syntaxBody3.teiTextSplitter();

        //_basicTokenInjector
        MorphologyParser basicMorphologyParser = new MorphologyParser();
        basicMorphologyParser.createToken("N", "le", 0, 2);
        basicMorphologyParser.createToken("N", "chien", 3, 8);
        basicMorphologyParser.createToken("N", "mange", 9, 14);
        basicMorphologyParser.createToken("N", "des", 15, 18);
        basicMorphologyParser.createToken("N", "pommes", 19, 25);
        _basicTokenInjector = new MorphologyTokenizer(
                new StringBuilder("le chien mange des pommes"),
                new StringBuilder("<text>le chien mange des pommes</text>"),
                basicMorphologyParser
        );

        //insideTokenInjector
        MorphologyParser insideMorphologyParser = new MorphologyParser();
        insideMorphologyParser.createToken("N","le",0,2);
        insideMorphologyParser.createToken("N","chien",3,8);
        insideMorphologyParser.createToken("N","mange",9,14);
        insideMorphologyParser.createToken("N","des",15,18);
        insideMorphologyParser.createToken("N","pommes",19,25);
        _insideTokenInjector = new MorphologyTokenizer(
                new StringBuilder("le chien mange des pommes"),
                new StringBuilder("<text>le <hi>chi</hi><hi>en</hi> mange de<s>s</s> <hi>pommes</hi></text>"),
                insideMorphologyParser
        );

        MorphologyParser insideMorphologyParser2 = new MorphologyParser();
        insideMorphologyParser2.createToken("N","le",0,2);
        insideMorphologyParser2.createToken("N","chien",3,8);
        insideMorphologyParser2.createToken("N","mange",9,14);
        insideMorphologyParser2.createToken("N","des",15,18);
        insideMorphologyParser2.createToken("N","(",19,20);
        insideMorphologyParser2.createToken("N","bonnes",20,26);
        insideMorphologyParser2.createToken("N",")",26,27);
        insideMorphologyParser2.createToken("N","pommes",28,34);
        _insideTokenInjector2 = new MorphologyTokenizer(
                new StringBuilder("le chien mange des (bonnes) pommes"),
                new StringBuilder("<text>le <hi>chi</hi><hi>en</hi> mange de<s>s</s> <hi>(bonnes<hi>)</hi> pommes</hi></text>"),
                insideMorphologyParser2
        );

        //_commentTokenInjector

        MorphologyParser commentMorphologyParser = new MorphologyParser();
        commentMorphologyParser.createToken("N", "le", 0, 2);
        commentMorphologyParser.createToken("N", "chien", 3, 8);
        commentMorphologyParser.createToken("N", "mange", 9, 14);
        commentMorphologyParser.createToken("N", "des", 15, 18);
        commentMorphologyParser.createToken("N", "pommes", 19, 25);
        _commentTokenInjector = new MorphologyTokenizer(
                new StringBuilder("le chien mange des pommes"),
                new StringBuilder("<text>le<!--testtest--> <hi>chi</hi>en" +
                        " <!--test-->mange de<s>s</s><!--lalalal--><!--test--> " +
                        "<hi>pommes</hi><!--lalala--></text>"),
                commentMorphologyParser
        );

        //_symbolTokenInjector
        MorphologyParser symbolMorphologyParser = new MorphologyParser();

        _symbolTokenInjector = new MorphologyTokenizer(
                new StringBuilder("le &amp; &amp; chi&eacute;ien ma&diams;nge des pommes&amp;"),
                new StringBuilder("le &amp; &amp; chi&eacute;ien ma&diams;nge des pommes&amp;"),
                symbolMorphologyParser
        );

        MorphologyParser symbolMorphologyParser2 = new MorphologyParser();
        symbolMorphologyParser2.createToken("N", "le", 0, 2);
        symbolMorphologyParser2.createToken("N", "&amp;", 3, 4);
        symbolMorphologyParser2.createToken("N", "&amp;", 5, 6);
        symbolMorphologyParser2.createToken("N", "chien", 7, 14);
        symbolMorphologyParser2.createToken("N", "mange", 15, 21);
        symbolMorphologyParser2.createToken("N", "des", 22, 26);
        symbolMorphologyParser2.createToken("N", "pommes", 27, 34);
        _symbolTokenInjector2 = new MorphologyTokenizer(
                new StringBuilder("le &amp; &amp; chi&eacute;ien ma&diams;nge &diams;des pommes&amp;"),
                new StringBuilder("<text>le &amp; &amp; chi&eacute;ien ma&diams;nge &diams;des pommes&amp;</text>"),
                symbolMorphologyParser2
        );

        MorphologyParser symbolMorphologyParser3 = new MorphologyParser();
        symbolMorphologyParser3.createToken("N", "le", 0, 2);
        symbolMorphologyParser3.createToken("N", "&amp;", 3, 4);
        symbolMorphologyParser3.createToken("N", "&amp;", 5, 6);
        symbolMorphologyParser3.createToken("N", "chien", 7, 14);
        symbolMorphologyParser3.createToken("N", "mange", 15, 22);
        symbolMorphologyParser3.createToken("N", "des", 23, 27);
        symbolMorphologyParser3.createToken("N", "pommes", 28, 35);
        _symbolTokenInjector3 = new MorphologyTokenizer(
                new StringBuilder("le &amp; &amp; chi&eacute;ien ma&diams;&diams;nge &diams;des pommes&amp;"),
                new StringBuilder("<text><hi>le</hi> &amp; &amp; <hi>chi</hi>&eacute;ien" +
                        " <hi>ma</hi><sub>&diams;&diams;</sub><sub>nge</sub> " +
                        "<sub>&diams;d</sub>es " +
                        "<hi>pommes&amp;</hi>" +
                        "</text>"),
                symbolMorphologyParser3
        );


        MorphologyParser alignmentMorphologyParser = new MorphologyParser();
        alignmentMorphologyParser.createToken("N", "le", 0, 2);
        alignmentMorphologyParser.createToken("N", "chien", 3 , 8);
        alignmentMorphologyParser.createToken("N", "mange", 9, 14);
        alignmentMorphologyParser.createToken("N", "un", 15, 17);
        alignmentMorphologyParser.createToken("N", "fromage", 18, 25);
        alignmentMorphologyParser.createToken("N", "assez", 26, 31);
        alignmentMorphologyParser.createToken("N", "délicieux", 33, 42);
        alignmentMorphologyParser.createToken("N", "<", 48, 49);
        _alignmentTokenInjector = new MorphologyTokenizer(
                new StringBuilder("le chien\nmange un fromage assez\n\ndélicieux  \n\n\n\n<"),
                new StringBuilder("<text><head>le chien</head><p>mange " +
                        "<div>un froma<sup>ge</sup> assez" +
                        "</div></p><p>d&eacute;licieux  </p>\n\n\n&lt;</text>"),
                alignmentMorphologyParser
        );
        _alignmentTokenInjector.teiWordTokenizer();

        MorphologyParser alignmentMorphologyParser2 = new MorphologyParser();
        alignmentMorphologyParser2.createToken("N", "le", 0, 2);
        alignmentMorphologyParser2.createToken("N", "chien", 3 , 8);
        alignmentMorphologyParser2.createToken("N", "mange", 9, 14);
        alignmentMorphologyParser2.createToken("N", "<", 16, 17);
        alignmentMorphologyParser2.createToken("N", "fromage", 18, 25);
        alignmentMorphologyParser2.createToken("N", "assez", 26, 31);
        alignmentMorphologyParser2.createToken("N", "délicieux", 33, 42);
        _alignmentTokenInjector2 = new MorphologyTokenizer(
                new StringBuilder("le chien\nmange \n< fromage assez\n\ndélicieux  \n\n\n\n"),
                new StringBuilder("<text><head>le chien</head><p>mange " +
                        "<div>&lt; froma<sup>ge</sup> assez" +
                        "</div></p><p>d&eacute;licieux  </p>\n\n\n</text>"),
                alignmentMorphologyParser2
        );
            _alignmentTokenInjector2.teiWordTokenizer();

        _offsetIdAlignment =  new ArrayList<>();
        _offsetIdAlignment.add("0, 2, [1]");
        _offsetIdAlignment.add("3, 8, [2]");
        _offsetIdAlignment.add("9, 14, [3]");
        _offsetIdAlignment.add("15, 17, [4]");
        _offsetIdAlignment.add("18, 25, [5, 6]");
        _offsetIdAlignment.add("26, 31, [7]");
        _offsetIdAlignment.add("33, 42, [8]");
        _offsetIdAlignment.add("48, 49, [9]");
    }
    @Test
    public void teiBodyspliterTest() throws Exception {
        Assert.assertEquals("this StringBuilder must be equals to :",
                _syntaxBody.getXml().toString(), _expectedStringBuilder);
        Assert.assertEquals("this StringBuilder must be equals to :",
                _syntaxBody2.getXml().toString(), _expectedStringBuilder2);
    }

    @Test
    public void basictokenInjectorTest() throws Exception {
        _basicTokenInjector.teiWordTokenizer();

        Assert.assertEquals("tokenizeInjector basic test fail :",
                "<text>" +
                        "<w xml:id=\"t1\">le</w> " +
                        "<w xml:id=\"t2\">chien</w> " +
                        "<w xml:id=\"t3\">mange</w> " +
                        "<w xml:id=\"t4\">des</w> " +
                        "<w xml:id=\"t5\">pommes</w>" +
                        "</text>",
                _basicTokenInjector.getTokenizeBuffer().toString()
        );
    }

    @Test
    public void insideTokenInjectorTest() throws Exception {
        _insideTokenInjector.teiWordTokenizer();
        Assert.assertEquals("tokenizeInjector inside test fail :",
                "<text>" +
                        "<w xml:id=\"t1\">le</w> " +
                        "<hi><w xml:id=\"t2\">chi</w></hi>" +
                        "<hi><w xml:id=\"t3\">en</w></hi> " +
                        "<w xml:id=\"t4\">mange</w> " +
                        "<w xml:id=\"t5\">de</w>" +
                        "<s><w xml:id=\"t6\">s</w></s> " +
                        "<hi><w xml:id=\"t7\">pommes</w></hi>" +
                        "</text>",
                _insideTokenInjector.getTokenizeBuffer().toString()
        );

        _insideTokenInjector2.teiWordTokenizer();
        Assert.assertEquals("tokenizeInjector inside test fail :",
                "<text>" +
                        "<w xml:id=\"t1\">le</w> " +
                        "<hi><w xml:id=\"t2\">chi</w></hi>" +
                        "<hi><w xml:id=\"t3\">en</w></hi> " +
                        "<w xml:id=\"t4\">mange</w> " +
                        "<w xml:id=\"t5\">de</w>" +
                        "<s><w xml:id=\"t6\">s</w></s> " +
                        "<hi><w xml:id=\"t7\">(</w>" +
                        "<w xml:id=\"t8\">bonnes</w>" +
                        "<hi><w xml:id=\"t9\">)</w></hi> " +
                        "<w xml:id=\"t10\">pommes</w></hi>" +
                        "</text>",
                _insideTokenInjector2.getTokenizeBuffer().toString()
        );
    }

    @Test
    public void commentTokenInjector() throws Exception {
        _commentTokenInjector.teiWordTokenizer();
        Assert.assertEquals("tokenizeInjector comment test fail :",
                "<text>" +
                        "<w xml:id=\"t1\">le</w><!--testtest--> " +
                        "<hi><w xml:id=\"t2\">chi</w></hi>" +
                        "<w xml:id=\"t3\">en</w> " +
                        "<!--test--><w xml:id=\"t4\">mange</w> " +
                        "<w xml:id=\"t5\">de</w>" +
                        "<s><w xml:id=\"t6\">s</w></s><!--lalalal--><!--test--> " +
                        "<hi><w xml:id=\"t7\">pommes</w></hi><!--lalala-->" +
                        "</text>",
                _commentTokenInjector.getTokenizeBuffer().toString()
        );
    }

    @Test
    public void checkIfSymbolTest() throws Exception {
        _symbolTokenInjector.teiWordTokenizer();
        _symbolTokenInjector2.teiWordTokenizer();
        _symbolTokenInjector3.teiWordTokenizer();

        Assert.assertEquals("symbol execute test fails :",
                "<text>" +
                        "<w xml:id=\"t1\">le</w> " +
                        "<w xml:id=\"t2\">&amp;</w> " +
                        "<w xml:id=\"t3\">&amp;</w> " +
                        "<w xml:id=\"t4\">chi&eacute;ien</w> " +
                        "<w xml:id=\"t5\">ma&diams;nge</w> " +
                        "<w xml:id=\"t6\">&diams;des</w> " +
                        "<w xml:id=\"t7\">pommes&amp;</w>" +
                        "</text>",
                _symbolTokenInjector2.getTokenizeBuffer().toString());

        Assert.assertEquals("complex symbol execute test fails :",
                "<text>" +
                        "<hi><w xml:id=\"t1\">le</w></hi> " +
                        "<w xml:id=\"t2\">&amp;</w> " +
                        "<w xml:id=\"t3\">&amp;</w> " +
                        "<hi><w xml:id=\"t4\">chi</w></hi>" +
                        "<w xml:id=\"t5\">&eacute;ien</w> " +
                        "<hi><w xml:id=\"t6\">ma</w></hi>" +
                        "<sub><w xml:id=\"t7\">&diams;&diams;</w></sub>" +
                        "<sub><w xml:id=\"t8\">nge</w></sub> " +
                        "<sub><w xml:id=\"t9\">&diams;d</w></sub>" +
                        "<w xml:id=\"t10\">es</w> " +
                        "<hi><w xml:id=\"t11\">pommes&amp;</w></hi>" +
                        "</text>",
                _symbolTokenInjector3.getTokenizeBuffer().toString());

    }

    @Test
    public void checkTextAlignmentTest(){
        Assert.assertEquals("text alignment test fails :",
                "<text>" +
                        "<head><w xml:id=\"t1\">le</w> " +
                        "<w xml:id=\"t2\">chien</w></head>" +
                        "<p><w xml:id=\"t3\">mange</w> " +
                        "<div><w xml:id=\"t4\">un</w> " +
                        "<w xml:id=\"t5\">froma</w>" +
                        "<sup><w xml:id=\"t6\">ge</w></sup> " +
                        "<w xml:id=\"t7\">assez</w></div></p>" +
                        "<p><w xml:id=\"t8\">d&eacute;licieux</w>  </p>\n\n\n" +
                        "<w xml:id=\"t9\">&lt;</w>" +
                        "</text>",
                _alignmentTokenInjector.getTokenizeBuffer().toString());

        Assert.assertEquals("text alignment test fails :",
                "<text>" +
                        "<head><w xml:id=\"t1\">le</w> " +
                        "<w xml:id=\"t2\">chien</w></head>" +
                        "<p><w xml:id=\"t3\">mange</w> " +
                        "<div><w xml:id=\"t4\">&lt;</w> " +
                        "<w xml:id=\"t5\">froma</w>" +
                        "<sup><w xml:id=\"t6\">ge</w></sup> " +
                        "<w xml:id=\"t7\">assez</w></div></p>" +
                        "<p><w xml:id=\"t8\">d&eacute;licieux</w>  </p>\n\n\n" +
                        "</text>",
                _alignmentTokenInjector2.getTokenizeBuffer().toString());
    }

    @Test
    public void checkOffsetId(){

        _alignmentTokenInjector.getOffsetId().forEach(
                offsetId -> {
                    String observed = offsetId.getBegin() + ", " + offsetId.getEnd() + ", " + offsetId.getIds();
                    String expected = _offsetIdAlignment.get(_alignmentTokenInjector.getOffsetId().indexOf(offsetId));
                    Assert.assertEquals("this offset must be equals", expected, observed);
                }
        );
    }

}