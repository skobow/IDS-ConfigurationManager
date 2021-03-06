package de.fraunhofer.isst.configmanager.petrinet.builder;

import de.fraunhofer.iais.eis.AppRoute;
import de.fraunhofer.iais.eis.Endpoint;
import de.fraunhofer.iais.eis.RouteStep;
import de.fraunhofer.isst.configmanager.petrinet.model.*;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provide static methods, to generate a Petri Net (https://en.wikipedia.org/wiki/Petri_net) from an Infomodel AppRoute.
 */
public class InfomodelPetriNetBuilder {

    /**
     * Generate a Petri Net from a given infomodel {@link AppRoute}.
     * RouteSteps will be represented as Places, Endpoints as Transitions.
     *
     * @param appRoute an Infomodel {@link AppRoute}
     * @return a Petri Net created from the AppRoute
     */
    public static PetriNet petriNetFromAppRoute(AppRoute appRoute, boolean includeAppRoute){

        //create sets for places, transitions and arcs
        var places = new HashMap<URI, Place>();
        var transitions = new HashMap<URI, Transition>();
        var arcs = new HashSet<Arc>();

        if(includeAppRoute){
            //create initial place from AppRoute
            var place = new PlaceImpl(appRoute.getId());
            places.put(place.getID(), place);

            //for every AppRouteStart create a Transition and add AppRouteStart -> AppRoute
            for(var endpoint : appRoute.getAppRouteStart()){
                var trans = getTransition(transitions, endpoint);
                var arc = new ArcImpl(trans, place);
                arcs.add(arc);
            }

            //for every AppRouteEnd create a Transition and add AppRoute -> AppRouteEnd
            for(var endpoint : appRoute.getAppRouteEnd()){
                var trans = getTransition(transitions, endpoint);
                var arc = new ArcImpl(place, trans);
                arcs.add(arc);
            }
        }

        //add every SubRoute of the AppRoute to the PetriNet
        for(var subroute : appRoute.getHasSubRoute()){
            addSubRouteToPetriNet(subroute, arcs, places, transitions);
        }

        //create a PetriNet with all Arcs, Transitions and Places from the AppRoute
        var nodes = new HashSet<Node>();
        nodes.addAll(places.values());
        nodes.addAll(transitions.values());
        var petriNet = new PetriNetImpl(appRoute.getId(), nodes, arcs);
        addFirstAndLastNode(petriNet);
        return petriNet;
    }

    /**
     * Add a {@link RouteStep} to the Petri Net as a new Subroute.
     *
     * @param subRoute the subRoute that will be added to the current Petri Net
     * @param arcs list of arcs of the current Petri Net
     * @param places list of places of the current Petri Net
     * @param transitions list of transitions of the current Petri Net
     */
    private static void addSubRouteToPetriNet(RouteStep subRoute, Set<Arc> arcs, Map<URI, Place> places, Map<URI, Transition> transitions){

        //if a place with subroutes ID already exists in the map, the SubRoute was already added to the Petri Net
        if(places.containsKey(subRoute.getId())){
            return;
        }

        //create a new place from the subRoute
        var place = new PlaceImpl(subRoute.getId());
        places.put(place.getID(), place);

        //for every AppRouteStart create a transition and add AppRouteStart -> SubRoute
        for(var endpoint : subRoute.getAppRouteStart()){
            var trans = getTransition(transitions, endpoint);
            var arc = new ArcImpl(trans, place);
            arcs.add(arc);
        }

        //for every AppRouteEnd create a transition and add SubRoute -> AppRouteEnd
        for(var endpoint : subRoute.getAppRouteEnd()){
            var trans = getTransition(transitions, endpoint);
            var arc = new ArcImpl(place, trans);
            arcs.add(arc);
        }
    }

    /**
     * Get the transition for the given {@link Endpoint} by ID, or generate a new one if no transition for that endpoint exists.
     *
     * @param transitions the transition that will be created or found in the map
     * @param endpoint the endpoint for which the transition should be found
     * @return the existing transition with id from the map, or a new transition
     */
    private static Transition getTransition(Map<URI, Transition> transitions, Endpoint endpoint){
        if(transitions.containsKey(endpoint.getId())){
            return transitions.get(endpoint.getId());
        }else{
            var trans = new TransitionImpl(endpoint.getId());
            transitions.put(trans.getID(), trans);
            return trans;
        }
    }
    
    /**
     * Add a source node to every transition without input and a sink node to every transition without output.
     *
     * @param petriNet
     */
    private static void addFirstAndLastNode(PetriNet petriNet){
        var first = new PlaceImpl(URI.create("place://source"));
        first.setMarkers(1);
        var last = new PlaceImpl(URI.create("place://sink"));
        for(var node : petriNet.getNodes()){
            if(node instanceof TransitionImpl){
                //if node has no arc with itself as target, add arc: first->node
                if(node.getTargetArcs().isEmpty()){
                    var arc = new ArcImpl(first, node);
                    petriNet.getArcs().add(arc);
                }
                //if node has no arc with itself as source, add arc: node->last
                if(node.getSourceArcs().isEmpty()){
                    var arc = new ArcImpl(node, last);
                    petriNet.getArcs().add(arc);
                }
            }
        }
        petriNet.getNodes().add(first);
        petriNet.getNodes().add(last);
    }

}
