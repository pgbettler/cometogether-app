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
      :zoom="15"
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
import { setTimeout } from 'timers';
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
      locations: [],
      currentPlace: null,
      filterKey: "",
    };
  },  
  components: {
    MapFilter
  },
  computed: {
    ...mapState(['userProfile', 'currentUser', 'posts', 'likedPosts']),
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
    setPlaces() {
      console.log("WE'VE ENTERED SET PLACES: STAGE 1");
      let posts = fb.postsCollection;

      let locations = this.filterEvents();
      locations.forEach(doc => {
          //Make the marker
          console.log("Loc: " + doc.location + "Lat: " + doc.lat + "Lng: " + doc.lng);
          if (doc.location) {
            const marker = {
              lat: doc.lat,
              lng: doc.lng
            };
            this.markers.push({ position: marker });
            this.places.push(doc.location);
            this.center = marker;
            this.currentPlace = null;
          }
            console.log("Markers: " + this.markers.length)
      })
      /*
      setTimeout( () => {
        // create Markers for each PostID in array
        // postId.location 

        for(var i = 0; i < locations.length; i++) {

          console.log("Theoretically Printing Out Locations...");
          console.log(locations[i]);
          let postdoc = posts.doc(locations[i]);
          this.setLocationInfo(postdoc);
        }
      },2000);*/
    },
    setLocationInfo(postdoc) {
      var vm = this;
      let p = new Promise((resolve, reject) => {
        postdoc.get().then(function(doc){
          if (doc.exists) {

            let loc = doc.data().location;
            let lat = doc.data().lat;
            let lng = doc.data().lng;

            //Make the marker
            console.log("Loc: " + loc + "Lat: " + lat + "Lng: " + lng);
            if (loc) {
              const marker = {
                lat: lat,
                lng: lng
              };
              vm.markers.push({ position: marker });
              vm.places.push(loc);
              vm.center = marker;
              vm.currentPlace = null;
            }
            console.log("Markers: " + vm.markers.length)

          } else 
            console.log("WHY THE FUCK DOESNT THIS EXIST");
        });
        resolve('Success!'); 
      });
      p.then((message) => {
        console.log(message) 
        //this.addPostMarker();
      });

    },
    onFilterClick(value){
      this.filterKey = value;
      console.log("HEY ITS WORKING");
      // call setPlaces to load markers onto map
      this.setPlaces();
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
    addPostMarker() {
      var vm = this;
      console.log("Loc: " + vm.loc + "Lat: " + vm.lat + "Lng: " + vm.lng);
      if (this.loc) {
        const marker = {
          lat: this.lat,
          lng: this.lng
        };
        this.markers.push({ position: marker });
        this.places.push(this.loc);
        this.center = marker;
        this.currentPlace = null;
      }
      console.log("Markers: " + this.markers.length)
    },

    filterEvents() {
      console.log("WE'VE ENTERED FILTERED EVENTS: STAGE 2");
      this.markers = [];

      let uid = this.currentUser.uid;
      let likes = fb.likesCollection;
      let posts = fb.postsCollection;
      //For When We have the follows feature implemented
      //let follows = fb.collection("follows");

      //Pull key from component data
      let key = this.filterKey;
      // generate array of Post ID's to display as markers
      //fill in the empty Locations array to do so
      //Check Key for each type of filter
      if(key == 'liked')
      {
        // Only show the liked posts of that user
        // query the databse for User liked posts
        this.locations = this.likedPosts;
        return this.locations;
      } 
      else if(key == 'all')
      {
        this.locations = this.posts;
        return this.locations;
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