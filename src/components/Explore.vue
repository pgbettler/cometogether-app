<template>
    <div id="explore">
        <section>
            <div class="col2">
              <div v-if="posts.length">
                  <div v-for="post in posts" class="post">
                      <h5>{{ post.title }}</h5>
                      <span>{{ post.createdOn | formatDate }}</span>
                      <p>{{ post.content | trimLength }}</p>
                      <ul>
                          <li><a>likes {{ post.likes }}</a></li>
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
        title: '',
        content: '',
        eventDate: '',
        picture: ''
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
  }
};
</script>
