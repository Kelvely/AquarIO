package ink.aquar.util.misc.placeholder;

import java.util.*;

class Section implements Component {

    private final Section template;

    Section() {
        template = this;
    }

    private Section(Section template) {
        this.template = template;
    }

    private final Map<String, Set<Variable>> variables = new HashMap<>();

    private final Map<Variable, String> variablesAnti = new HashMap<>();

    private final Set<Loop> loops = new HashSet<>();

    private final List<Component> components = new LinkedList<>();

    public boolean isTemplateSame(Section section) {
        return section.template == template;
    }

    void setAllVariable(String name, Object value) {
        for(Loop loop : loops) {
            loop.setAllVariable(name, value);
        }
        Set<Variable> vars = variables.get(name);
        if(vars != null) {
            for(Variable var : vars) {
                var.setValue(value);
            }
        }

    }

    void setVariable(String name, Object value, int[] loopOccurrence, int depth) {
        if(depth < loopOccurrence.length) {
            for (Loop loop : loops) {
                loop.setVariable(name, value, loopOccurrence, depth);
            }
        } else {
            setAllVariable(name, value);
        }
    }

    /*
     * Gotta do some opts, create a set contains variable names in loops
     * Though it is gonna bring a bursting performance promotion but I don't wanna do this right now cuz im lazy xDD
     */
    boolean containsVariable(String name) {
        if(variables.containsKey(name)) return true;
        for(Loop loop : loops) {
            if(loop.getTemplate().containsVariable(name)) return true;
        }
        return false;
    }

    Section createAsTemplate() {
        Section section = new Section(this);
        cloneTemplateInto(section);
        return section;
    }

    void cloneTemplateInto(Section section) {
        for(Component component : components) {
            if(component instanceof Loop) {
                Loop loop = new Loop(((Loop) component).getTemplate().createAsTemplate());
                section.components.add(loop);
                section.loops.add(loop);
            } else if(component instanceof Variable) {
                Variable var = new Variable();
                String name = variablesAnti.get(component);
                section.variablesAnti.put(var, name);
                section.variables.computeIfAbsent(name, k -> new HashSet<>()).add(var);
                section.components.add(new Variable());
            } else {
                section.components.add(component);
            }
        }
    }

    static void parse(Section section, String text, BakedProfile profile) {
        // TODO
    }

    @Override
    public List<Object> dump() {
        List<Object> list = new LinkedList<>();
        for(Component component : components) {
            list.addAll(component.dump());
        }
        return list;
    }
}
