package module.termsuite;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import static eu.project.ttc.readers.JsonCasConstants.*;

/**
 * @author Simon Meoni
 *         Created on 25/08/16.
 */
public class TermsuiteJsonReader {

    private final static Logger LOGGER = LoggerFactory.getLogger(TermsuiteJsonReader.class);
    private static JsonFactory factory = new JsonFactory();
    private Queue<Token> tokenQueue;
    private File file;


    public TermsuiteJsonReader(){
        this.tokenQueue = new LinkedList<>();
    }

    public TermsuiteJsonReader(File file) {this(new LinkedList<>(), file);}

    private TermsuiteJsonReader(Queue<Token> tokenQueue, File file) {
        this.tokenQueue = tokenQueue;
        this.file = file;
    }

    Queue<Token> getTokenQueue() {
        return tokenQueue;
    }

    void setTokenQueue(Queue<Token> tokenQueue) {
        this.tokenQueue = tokenQueue;
    }

    public void parsing() {
        try {
            browseJson();
            clean();
        } catch (IOException e) {
            LOGGER.error("An error occurred during TermSuite Json Cas parsing", e);
        }
    }

    private void browseJson() throws IOException {
        JsonParser parser = factory.createParser(file);

        JsonToken jsonToken;
        Token token = new Token();
        boolean inWa = false;

        while ((jsonToken = parser.nextToken()) != null) {

            if (inWa){
                if (jsonToken == JsonToken.END_ARRAY)
                    break;
                else if (jsonToken == JsonToken.END_OBJECT) {
                    tokenQueue.add(token);
                    token = new Token();
                }
                fillTokenStack(parser, jsonToken, token);
            }

            else if ("word_annotations".equals(parser.getParsingContext().getCurrentName())) {
                inWa = true;
            }
        }
    }

    private void fillTokenStack(JsonParser parser, JsonToken jsonToken, Token token) throws IOException {
        if (jsonToken.equals(JsonToken.FIELD_NAME)){
            switch (parser.getCurrentName()){
                case F_LEMMA :
                    token.setLemma(parser.nextTextValue());
                    break;
                case F_TAG :
                    token.setPos(parser.nextTextValue());
                    break;
                case F_BEGIN :
                    token.setBegin(parser.nextIntValue(0));
                    break;
                case F_END :
                    token.setEnd(parser.nextIntValue(0));
                    break;
                default:
                    break;
            }
        }
    }

    void clean(){
        Token lastToken = new Token();

        for (Token token : tokenQueue) {

            if (token.begin != 0) {
                if (token.begin == lastToken.end) {
                    lastToken.end--;
                }
                if (token.begin < lastToken.end) {
                    token.begin = lastToken.end + 1;
                }
            }
            lastToken = token;
        }
    }

    public void createToken(String pos, String lemma, int begin, int end){
        tokenQueue.add(new Token(pos, lemma, begin, end));
    }

    class Token {

        public String pos;
        private String lemma;
        private int begin;
        private int end;

        Token(String pos, String lemma, int begin, int end) {
            this.pos = pos;
            this.lemma = lemma;
            this.begin = begin;
            this.end = end;
        }

        Token(){}

        public void setPos(String pos) {
            this.pos = pos;
        }

        public void setLemma(String lemma) {
            this.lemma = lemma;
        }

        public void setBegin(int begin) {
            this.begin = begin;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public String getPos() {
            return pos;
        }

        public String getLemma() {
            return lemma;
        }

        public int getBegin() {
            return begin;
        }

        public int getEnd() {
            return end;
        }
    }
}