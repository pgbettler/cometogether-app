<!---

This is one singular post

Information needs to be passed in about what post this is

-->

<template>
    <div class="Post">
        <div class = "postcontent">
            <h4>{{ this.post.title }}</h4>
            <h5>{{ this.post.organizationName }}</h5>
            <span>{{ this.post.eventDate | moment }}</span>
            <p>{{ this.post.content | trimLength }}</p>
            
            <!-- Maybe add a unlike button instead of like button since it's already liked -->
            <button class="button">Likes {{ post.likeCount }}</button> <!-- They can only view likes -->
        </div>

        <div v-if="showUpdateButtons" class="updatebuttons">
            <ul>
                <button @click="deletePost(post.id)" class="button">Delete Post</button>
                <button v-on:click="$emit('editPost', this.post)" class="button">Edit Post</button>
            </ul>
        </div>
    </div>
</template>

<script>
import { mapState } from "vuex";
const fb = require("../../../firebaseConfig.js");

export default {
    props: ['post', 'showUpdateButtons']
}




</script>