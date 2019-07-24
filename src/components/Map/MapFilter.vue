<!--

This is the map filter that tells the map what to show

Read in: an array of posts or current filter selections

Write out: an array of posts or new filter selections

-->


<template>
  <div id="filter"> 
    <h3>Filter Map:</h3>
    <div class="filter-buttons">
      <div id="filterDrop">
    <select class="button" name="filterlist" v-model= "selected">
        <option disabled value="">Please select one</option>
        <option value="liked">Liked Posts</option>
        <option value="all">All Posts</option>
    </select> 
    </div>

    <div id="filterButton"> <button class="button" v-on:click=select()>Filter!</button> </div>
    </div>
    
    
    <!-- <span>Selected: {{ selected }}</span> -->
  

  </div>
</template>

<script>
  export default {
    data() {
      return {
        selected: ''
      }
    },
    methods: {
      select() {
        this.$emit('filterToMap', this.selected);
      },



    }
  }
</script>

<style>

  #filterDrop {
    float:left;
    
  }
  #filterButton {
    
    float:right;
  }

#filter {
    border-radius: 20px;
    padding: 10px 40px 60px 40px;
    border-style:solid;
    border-color: darkgrey;
    width: 40%;
    text-align: center;
    background-color:rgba(255, 250, 250, 0.932)
}

</style>