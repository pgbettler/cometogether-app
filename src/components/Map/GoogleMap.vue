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
        @click="toggleInfoWindow(m,i)"
      ></gmap-marker>
      <gmap-info-window :options="infoOptions" :position="infoWindowPos" :opened="infoWinOpen" @closeclick="infoWinOpen=false">
        <!-- your custom html components -->
        <div id="Marker">
          <img :src=this.infoPictureUrl id="pics" height="100" class="post-picture" >
          <h3>{{this.infoTitle}}</h3>
          <h4>{{this.infoLocation}}</h4>
          <span>{{ this.infoDate | moment }}</span>
          <p>{{this.infoContent}}</p>
        </div>
        
        </gmap-info-window>
    </gmap-map>
  </div>
</template>

<script>
import moment from 'moment'; //this is used for date formatting
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
      infoContent: '',
      infoTitle: '',
      infoLocation: '',
      infoDate: '',
      infoPictureUrl: '',
      infoWindowPos: null,
      infoWinOpen: false,
      currentMidx: null,
      //optional: offset infowindow so it visually sits nicely on top of our marker
      infoOptions: {
          pixelOffset: {
          width: 0,
          height: -35
          }
      }
    };
  },  
  components: {
    MapFilter,
  },
  computed: {
    ...mapState(['userProfile', 'currentUser', 'posts', 'likedPosts']),
  },

  mounted() {
    this.geolocate();
  },

  filters: {
      moment: function(date) {
        return moment(date).format('MMMM Do YYYY, h:mm a');
      },
  },

  methods: {
    // receives a place object via the autocomplete component
    //Pass Id to marker class to decide generation of marker on map. make whether we fine the uid into a computed object possibly another component 
    setPlace(place) {
      this.currentPlace = place;
    }, 
    setPlaces() {
      let posts = fb.postsCollection;

      let locations = this.filterEvents();
      locations.forEach(doc => {
          //Make the marker
          console.log("Loc: " + doc.location + "Lat: " + doc.lat + "Lng: " + doc.lng);
          if (doc.lng) {
            const marker = {
              position:
              {
              lat: doc.lat,
              lng: doc.lng,
              },
              infoTitle: doc.title,
              infoContent: doc.content,
              infoDate: doc.eventDate,
              infoLocation: doc.location,
              infoPictureUrl: doc.pictureUrl
            };

            this.markers.push(marker);
            this.places.push(doc.location);
            this.center = marker;
            this.currentPlace = null;
          }
            console.log("Markers: " + this.markers.length)
      })
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
    },
    toggleInfoWindow: function(marker, idx) {
      this.infoWindowPos = marker.position;
      console.log("Marker Info: " + JSON.stringify(marker));
      this.infoTitle= marker.infoTitle;
      this.infoContent = marker.infoContent;
      this.infoDate = marker.Date;
      this.infoLocation = marker.infoLocation;
      this.infoPictureUrl = marker.infoPictureUrl;

      //check if its the same marker that was selected if yes toggle
      if (this.currentMidx == idx) {
        this.infoWinOpen = !this.infoWinOpen;
      }
      //if different marker set infowindow to open and reset current marker index
      else {
        this.infoWinOpen = true;
        this.currentMidx = idx;
      }
    }
  },
}
</script>

<style>
  #marker {
    align-content: left;
  }
</style>