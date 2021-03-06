package de.fraunhofer.isst.configmanager.petrinet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation class of the {@link Place} interface
 */
public class PlaceImpl implements Place {

    private transient URI id;
    private int markers;

    @JsonIgnore
    private transient Set<Arc> sourceArcs;

    @JsonIgnore
    private transient Set<Arc> targetArcs;

    public PlaceImpl(URI id){
        this.id = id;
        this.sourceArcs = new HashSet<>();
        this.targetArcs = new HashSet<>();
        this.markers = 0;
    }

    @Override
    public URI getID() {
        return id;
    }

    @Override
    public Set<Arc> getSourceArcs() {
        return sourceArcs;
    }

    @Override
    public Set<Arc> getTargetArcs() { return targetArcs; }

    @Override
    public boolean isComplementOf(Node other) {
        return Transition.class.isAssignableFrom(other.getClass());
    }

    @Override
    public Node deepCopy() {
        var copy = new PlaceImpl(this.getID());
        copy.setMarkers(this.getMarkers());
        return copy;
    }

    @Override
    public int getMarkers() {
        return markers;
    }

    @Override
    public void setMarkers(int markers) {
        this.markers = markers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaceImpl place = (PlaceImpl) o;
        return markers == place.markers &&
                Objects.equals(id, place.id);
    }

}
