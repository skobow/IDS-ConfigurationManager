package de.fraunhofer.isst.configmanager.petrinet.model;

/**
 * Implementation class of the {@link Arc} interface
 */
public class ArcImpl implements Arc {
    private Node source, target;

    public ArcImpl(Node source, Node target){
        if(source.isComplementOf(target)){
            this.source = source;
            source.getSourceArcs().add(this);
            this.target = target;
            target.getTargetArcs().add(this);
        }else{
            throw new IllegalArgumentException(
                    String.format(
                            "Node source is of type %s, target should be another Type!",
                            source.getClass().getSimpleName()
                    )
            );
        }
    }

    @Override
    public Node getSource() {
        return source;
    }

    @Override
    public Node getTarget() {
        return target;
    }

    @Override
    public void setSource(Node source) {
        if(target.isComplementOf(source)){
            //if given node is a different type as current target: set as source
            this.source.getSourceArcs().remove(this);
            this.source = source;
            source.getSourceArcs().add(this);
        }else{
            //if given node is same type as current target: throw an Exception
            throw new IllegalArgumentException(
                    String.format(
                            "Node target is of type %s, source should be another Type!",
                            target.getClass().getSimpleName()
                    )
            );
        }
    }

    @Override
    public void setTarget(Node target) {
        if(source.isComplementOf(target)){
            //if given node is a different type as current source: set as target
            this.target.getTargetArcs().remove(this);
            this.target = target;
            target.getTargetArcs().add(this);
        }else{
            //if given node is same type as current source: throw an Exception
            throw new IllegalArgumentException(
                    String.format(
                            "Node source is of type %s, target should be another Type!",
                            source.getClass().getSimpleName()
                    )
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArcImpl arc = (ArcImpl) o;
        return source.equals(arc.source) &&
                target.equals(arc.target);
    }

}
