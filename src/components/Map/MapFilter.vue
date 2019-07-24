<!--

This is the map filter that tells the map what to show

Read in: an array of posts or current filter selections

Write out: an array of posts or new filter selections

-->


<template>
  <div id="filter"> 
    <h3>Filter Map:</h3>
    <select class="button" name="filterlist" v-model= "selected">
        <option disabled value="">Please select one</option>
        <option value="liked">Liked Posts</option>
        <option value="all">All Posts</option>
    </select>
     <!-- <span>Selected: {{ selected }}</span> --><br><br>
    <button class="button" id="filterbutton" v-on:click=select()>Filter!</button>
   
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
#filter {
    padding: 5px;
    border-style: solid;
    width: 20%;
    text-align: center;
}
 #filter #filterbutton {
    width: 50%;
    height: 50%;
    }


</style>