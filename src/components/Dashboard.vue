<template>
    <div id="dashboard">
        <section>
            <div class="col1">
              <!-- Depending on User Type,
                 this seciton while show Student or Organization Sideview-->


              <!-- Student View:
                  FirstName LastName
                  Student

                  Liked Organizations List
              -->
                <div class="profile" v-if="userProfile.accountType == 'Student'">
                    <h5>{{ userProfile.firstName }} {{ userProfile.lastName }}</h5>
                    <p>{{ userProfile.accountType }}</p>
                    <div class="liked-org">
                        <p>This section will show liked organizations</p>
                    </div>
                </div>
              <!-- Organization View:
                  OrganizationName
                  Organization
                  Organization Description

                  Create New Post
              -->
                <div class="profile" v-if="userProfile.accountType == 'Organization'">
                    <h5>{{ userProfile.organizationName }}</h5>
                    <p>{{ userProfile.accountType }}</p>
                    <p>{{ userProfile.organizationDetails }}</p>
                    <div class="create-post">
                        <p>create a post</p>
                        <form @submit.prevent>
                            <textarea v-model.trim="post.title" placeholder="Event Name"></textarea><p>required</p>
                            <datetime v-model="date"></datetime>
                            <textarea v-model.trim="post.content" placeholder = "Details"></textarea>
                            <textarea v-model.trim="post.picture" placeholder = "Add Photo"></textarea>
                            <button @click="createPost" class="button">post</button>
                        </form>
                    </div>
                </div>
            </div>
            <div class="col2">
              <div v-if="posts.length">
                  <div v-for="post in posts" class="post">
                      <h5>{{ post.title }}</h5>
                      <h7>{{ post.eventDate }}</h7>
                      <p>{{ post.content | trimLength }}</p>
                      <ul>
                          <li><a @click="likePost(post.id, post.likeCount)">likes {{ post.likeCount }}</a></li>
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
        title: '',
        content: '',
        eventDate: '',
        picture: '',
        likeCount: ''
      },
      showEditForm: false,
      editId: "",
      search: "",
      contactString: ""
    };
  },
  computed: {
    ...mapState(['userProfile', 'currentUser', 'posts']),
  },
  methods: {
    createPost() {
      fb.postsCollection.add({
          title: this.post.title,
          createdOn: new Date(),
          eventDate: this.post.eventDate,
          content: this.post.content,
          picture: this.post.picture,
          userId: this.currentUser.uid,
          organizationName: this.userProfile.organizationName,
          likeCount: 0
        })
        .then(ref => {
          this.post.title = '',
          this.post.content = '',
          this.post.picture = '',
          this.post.eventDate = ''
        })
        .catch(err => {
          console.log(err);
        });
    },
    likePost(postId, postLikes) {
      let docId = `${this.currentUser.uid}_${postId}`
        fb.likesCollection.doc(docId).get().then(doc => {
          // add to users list of liked posts
          if (doc.exists) { return }
          fb.likesCollection.doc(docId).set({
             postId: postId,
             userId: this.currentUser.uid
          }).then(() => {
             // update post likes
             fb.postsCollection.doc(postId).update({
             likeCount: postLikes + 1
            })
          })
          }).catch(err => {
             console.log(err)
          })
   },
    deleteContact(id) {
      fb.contactsCollection
        .doc(id)
        .delete()
        .catch(err => {
          console.log(err);
        });
    },
    toggleForm() {
      // hides the appropriate forms at the button clicks
      this.showEditForm = !this.showEditForm;
    },
    saveEditContact() {
      // Calls toggle form at the end of all operations to hide the edit form
      //This method is called by Save to update a contact with info
      fb.postsCollection
        .doc(this.editId)
        .update({
          firstName: this.contact.firstName,
          lastName: this.contact.lastName,
          phoneNumber: this.contact.phoneNumber,
          email: this.contact.email,
          address: this.contact.address
        })
        .then(ref => {
          (this.contact.firstName = ""),
            (this.contact.lastName = ""),
            (this.contact.phoneNumber = ""),
            (this.contact.email = ""),
            (this.contact.address = "");
        })
        .catch(err => {
          console.log(err);
        });
      //hide the form
      this.toggleForm();
    },
    //this method is triggered by the edit button
    //toggles the form and populates the placeholders with contact information
    editContact(contact) {
      //make the edit form appear
      this.toggleForm();
      //populate with contact info
      console.log(this.contact.firstName);
      console.log("sanity check");
      (this.contact.firstName = contact.firstName),
        (this.contact.lastName = contact.lastName),
        (this.contact.phoneNumber = contact.phoneNumber),
        (this.contact.email = contact.email),
        (this.contact.address = contact.address),
        (this.editId = contact.id);
    }
  }
};
</script>
