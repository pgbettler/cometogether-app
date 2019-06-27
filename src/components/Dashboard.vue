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
                            <textarea></textarea>
                            <button class="button">post</button>
                        </form>
                    </div>
                </div>
            </div>
            <div class="col2">
                <div>
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
      contact: {
        firstName: "",
        lastName: "",
        phoneNumber: "",
        email: "",
        address: ""
      },
      showEditForm: false,
      editId: "",
      search: "",
      contactString: ""
    };
  },
  computed: {
    filteredContacts() {
      return this.contacts.filter(contact => {
        return (
          JSON.stringify(contact)
            .toLowerCase()
            .includes(this.search.toLowerCase()) ||
          JSON.stringify(contact.firstName + " " + contact.lastName)
            .toLowerCase()
            .includes(this.search.toLowerCase())
        );
      });
    },
    ...mapState(["userProfile", "currentUser", "contacts"]),
    filteredList() {
      return this.contacts.filter(contact => {
        return contact.firstName
          .toLowerCase()
          .includes(this.search.toLowerCase());
      });
    }
  },
  methods: {
    createContact() {
      fb.postsCollection
        .add({
          createdOn: new Date(),
          firstName: this.contact.firstName,
          lastName: this.contact.lastName,
          phoneNumber: this.contact.phoneNumber,
          email: this.contact.email,
          address: this.contact.address,
          userId: this.currentUser.uid,
          userName: this.userProfile.name
        })
        .then(ref => {
          (this.contact.firstName = ""),
            (this.contact.lastName = ""),
            (this.contact.phoneNumber = "");
          (this.contact.email = ""), (this.contact.address = "");
        })
        .catch(err => {
          console.log(err);
        });
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
