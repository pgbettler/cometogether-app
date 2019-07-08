<template>
    <div id="explore">
        <section>
            <div class="col2">
              <div v-if="posts.length">
                  <div v-for="post in posts" class="post">
                      <h5>{{ post.title }}</h5>
                      <!-- <span>{{ post.createdOn | formatDate }}</span> -->
                      <p>{{ post.content | trimLength }}</p>
                      <!-- <button class="button" @click="toggleLike">  Likes {{ post.likeCount }}</button> -->
                      <button class="button" @click="followOrg(post.userId)" id="follow">{{follow.text}}</button>
                      <ul>
                         <!-- <li><a>Likes: {{ post.likeCount }}</a></li> -->
                          <li><a>view full post</a></li>
                      </ul>
                  </div>
              </div> 
              <div v-else>
                  <p class="no-results">There are currently no posts</p>
              </div>
            </div>
        </section>
    </div>
</template>
<script>
import { mapState } from "vuex";
const fb = require("../../firebaseConfig.js");


export default {
  data() {
    return {
      post: {
        likeCount:''
      },
      search: "",
      contactString: "",
      performingRequest: false,
      follow: { 
        text: 'Follow Org'
      },
      followFlag: true,
    };
  },
  computed: {
    ...mapState(['userProfile', 'currentUser', 'posts']),
  },
  methods: {
      /*
      toggleLike() {
        // Need to grab userID and postID and also update like 
        fb.likesCollection
        .doc(this.editId)
        .update({
            likeCount: this.likeCount
        })
        .then(ref => {
            (this.contact.likeCount = "");
        })
        .catch(err => {
          console.log(err);
        });  
     }
     */
    followOrg(orgId){
      
      let uid = this.currentUser.uid;
      console.log(uid);
      console.log(orgId);
     if (this.followFlag == true) {
        this.follow.text="UnFollow";

        fb.followsCollection.add({
          orgId: orgId,
          userId: uid
        })
        .then(ref => {
          console.log("Document successfully written!");
          })
        .catch(err => {
          console.log(err);
          });
        this.followFlag = false;
     } else {
       this.follow.text = "Follow";

       var followsRef = fb.followsCollection.where("orgId", "==", orgId).where("userId", "==", uid);
       followsRef.get().then(function(querySnapshot) {
          querySnapshot.forEach(function(doc) {
            doc.ref.delete();
          });
       });
       this.followFlag = true;
     }
     
    
    }
  }
};
</script>
