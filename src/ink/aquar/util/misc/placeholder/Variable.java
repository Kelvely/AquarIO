package ink.aquar.util.misc.placeholder;


import java.util.Collections;
import java.util.List;

class Variable implements Component {

    private Object value;

    void setValue(Object value) {
        this.value = value;
    }

    @Override
    public List<Object> dump() {
        return Collections.singletonList(value);
    }
}
