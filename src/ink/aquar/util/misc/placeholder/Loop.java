package ink.aquar.util.misc.placeholder;

import java.util.*;

class Loop implements Component {

    private final Section template;

    private final List<Section> sections = new ArrayList<>();

    Loop(Section template) {
        this.template = template.createAsTemplate();
    }

    void setAllVariable(String name, Object value) {
        for(Section section : sections) {
            section.setAllVariable(name, value);
        }

    }

    void setVariable(String name, Object value, int[] loopOccurrence, int depth) {
        if(template.containsVariable(name)) {
            int occurrence = loopOccurrence[depth];
            while (occurrence >= sections.size()) {
                sections.add(template.createAsTemplate());
            }
            sections.get(occurrence).setVariable(name, value, loopOccurrence, depth + 1);
        }
    }

    Section getTemplate() {
        return template;
    }

    @Override
    public List<Object> dump() {
        List<Object> list = new LinkedList<>();
        for (Section section : sections) {
            list.addAll(section.dump());
        }
        return list;
    }

}
