package ink.aquar.util.misc.placeholder;

import java.util.Collections;
import java.util.List;

class Text implements Component {

    private final String content;

    Text(String content) {
        this.content = content;
    }

    @Override
    public List<Object> dump() {
        return Collections.singletonList(content);
    }
}
