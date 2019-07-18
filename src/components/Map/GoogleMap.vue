<!--

This will be the google map

It needs to accept filters from Map

Write Out: if a pin is clicked, can we send this back to the parent component to make something happen on the posts side?

-->

<template>
  <div>
    <!-- <div>
      <h2>Search and add a pin</h2>
      <label>
        <gmap-autocomplete
          @place_changed="setPlace">
        </gmap-autocomplete>
        <button @click="addMarker">Add</button>
      </label>
      <br/>

    </div> -->
    <MapFilter v-on:filterToMap="onFilterClick"></MapFilter>
    <br>
    <gmap-map
      :center="center"
      :zoom="14.75"
      style="width:100%;  height: 400px;"
    >
      <gmap-marker
        :key="index"
        v-for="(m, index) in markers"
        :position="m.position"
        @click="center=m.position"
      ></gmap-marker>
    </gmap-map>
  </div>
</template>

<script>
import { mapState } from "vuex";
import { likesCollection } from '../../../firebaseConfig';
import MapFilter from './MapFilter';
const fb = require("../../../firebaseConfig.js");

//addEvents(this.currentUser.uid);

export default {
  name: "GoogleMap",
  data() {
    return {
      // default to UCF to keep it simple
      center: { lat: 28.6027206, lng: -81.2015438 },
      markers: [],
      places: [],
      currentPlace: null,
      filterKey: ""
    };
  },  
  components: {
    MapFilter
  },
  computed: {
    ...mapState(['userProfile', 'currentUser', 'posts']),
  },

  mounted() {
    this.geolocate();
  },

  methods: {
    // receives a place object via the autocomplete component
    //Pass Id to marker class to decide generation of marker on map. make whether we fine the uid into a computed object possibly another component 
    setPlace(place) {
      this.currentPlace = place;
    }, 
    setPlaces(callback) {
      console.log("WE'VE ENTERED SET PLACES: STAGE 1");
      let posts = fb.postsCollection;
      let locations = callback();
      let locLen = locations.length;
      // create Markers for each PostID in array
      // postId.location 
      console.log("About to Loop on Locations...");
      console.log("But First Heres the array: ");
      console.log(locations);
      console.log("Length of Loc: " + locations.length);
      for(var i = 0; i < 8; i++) {
        console.log("Theoretically Printing Out Locations...");
        this.addPostMarker(posts.doc(locations[i]).location);
      }
    },
    onFilterClick(value){
      this.filterKey = value;
      console.log("HEY ITS WORKING");
      // call setPlaces to load markers onto map
      this.setPlaces(this.filterEvents);
    },
    addMarker() {
      if (this.currentPlace) {
        const marker = {
          lat: this.currentPlace.geometry.location.lat(),
          lng: this.currentPlace.geometry.location.lng()
        };
        this.markers.push({ position: marker });
        this.places.push(this.currentPlace);
        this.center = marker;
        this.currentPlace = null;
      }
    },
    
    addPostMarker(loc) {
      if (loc) {
        const marker = {
          lat: loc.geometry.location.lat(),
          lng: loc.geometry.location.lng()
        };
        this.markers.push({ position: marker });
        this.places.push(loc);
        this.center = marker;
        this.currentPlace = null;
      }
    },

    filterEvents() {
      console.log("WE'VE ENTERED FILTERED EVENTS: STAGE 2");

      let uid = this.currentUser.uid;
      let likes = fb.likesCollection;
      let posts = fb.postsCollection;
      //For When We have the follows feature implemented
      //let follows = fb.collection("follows");

      //Pull key from component data
      let key = this.filterKey;
      // generate array of Post ID's to display as markers
      //fill in the empty Locations array to do so
      let locations = [];

      //Check Key for each type of filter
      if(key == 'liked')
      {
        // Only show the liked posts of that user
        // query the databse for User liked posts
        var listLocs = likes.where("userId", "==", uid);
        //Store postId's in locations array
        listLocs.get().then(function(querySnapshot) 
        {
          if (querySnapshot.empty) {
            console.log('no documents found');
          } 
          else 
          {
            // Adds each result into locations array
            querySnapshot.forEach(function(doc) 
            { 
              //need to see how to get the postId out of these documents
              locations.push(doc.data().postId);
              // doc.data() is never undefined for query doc snapshots
              //console.log(doc.data().postId);
              //console.log(doc.id, " => ", doc.get(postId));
            });
            
          } 
        });
        //console.log("Locations: "+ locations);
        return locations;
      } 
      else if(key == 'all')
      {
        posts.get().then(function(querySnapshot) 
        {
          querySnapshot.forEach(function(doc)
          {
            //load postId's into location array
            locations.push(doc.data().postId);
            // doc.data() is never undefined for query doc snapshots
            console.log(doc.id, " => ", doc.data());
          });
        });
        //console.log("Locations: "+ locations);
        return locations;
      } 
      else if (key == 'follows') 
      {
        /* Commented out until Follow feature is implemented

        //Grab all Post ID's from all Posts by followed Organizations
        //Get List of Orgs user is Following 
        var following = follows.where("userId", "==", uid);
        follows.get().then(function(querySnapshot) 
        {
          // For each OrgId in the follow collection
          // Go through the orgs posts and push them to locations
          // doc here means "follow doc"
          querySnapshot.forEach(function(doc) 
          {
            // query posts for all posts by an org
            //using the orgId field of the current follow doc
            //Grab the posts made by that orgId

            var orgPosts = posts.where("userId", "==", doc.orgId);
            orgPosts.get().then(function(querySnapshot)
            {
              //Push each post ID onto the locations array
              querySnapshot.forEach(function(doc) 
              {
                locations.push(doc.id);  
              });
            }); 
          });
        });
        console.log("Locations: "+ locations);
        return locations;
        */
      }

    },
    geolocate: function() 
    {
      navigator.geolocation.getCurrentPosition(position => 
      {
        this.center = 
        {
          lat: position.coords.latitude,
          lng: position.coords.longitude
        };
      });
    }
  }
};
</script>