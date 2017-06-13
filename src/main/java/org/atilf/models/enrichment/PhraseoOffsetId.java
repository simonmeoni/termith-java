package org.atilf.models.enrichment;

import java.util.List;

/**
 * Created by Simon Meoni on 12/06/17.
 */
public class PhraseoOffsetId extends TermOffsetId{
    public PhraseoOffsetId(int begin, int end, int id, String word, List<Integer> ids) {
        super(begin, end, id, word);
        _ids = ids;
    }
}
