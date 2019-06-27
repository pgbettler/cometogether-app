<template>
  <div id="dashboard">
    <section>
      <div class="col1">
        <div class="profile">
          <h5>{{ userProfile.firstName }}'s Contacts</h5>
          <div class="edit-Contact" v-if="showEditForm">
            <p>Edit Contact</p>
            <form>
              <!-- //might need a submit prevent button -->
              <textarea v-model.trim="contact.firstName" placeholder="First Name"></textarea>
              <textarea v-model.trim="contact.lastName" placeholder="Last Name"></textarea>
              <textarea v-model.trim="contact.email" placeholder="Email"></textarea>
              <textarea v-model.trim="contact.phoneNumber" placeholder="Phone Number"></textarea>
              <textarea v-model.trim="contact.address" placeholder="Address"></textarea>
              <button @click="saveEditContact()" class="button">Save</button>
            </form>
          </div>
          <div class="create-contact" v-if="!showEditForm">
            <p>Create a Contact</p>
            <form @submit.prevent>
              <textarea v-model.trim="contact.firstName" placeholder="First Name"></textarea>
              <textarea v-model.trim="contact.lastName" placeholder="Last Name"></textarea>
              <textarea v-model.trim="contact.email" placeholder="Email"></textarea>
              <textarea v-model.trim="contact.phoneNumber" placeholder="Phone Number"></textarea>
              <textarea v-model.trim="contact.address" placeholder="Address"></textarea>
              <button @click="createContact" class="button">Create</button>
            </form>
          </div>
        </div>
      </div>
      <div class="col2">
        <div class="container">
          <input type="text" v-model="search" placeholder="Search..">
          <div class="search"></div>
        </div>
        <div v-if="contacts.length">
          <div v-for="contact in filteredContacts" :key="contact.id" class="contact">
            <h3>{{ contact.firstName }} {{ contact.lastName }}</h3>
            <h3>{{ contact.phoneNumber}}</h3>
            <h5>{{ contact.email }}</h5>
            <h5>{{ contact.address }}</h5>
            <ul>
              <li>
                <button @click="editContact(contact)" class="button">Edit</button>
              </li>
              <li>
                <button @click="deleteContact(contact.id)" class="button">Delete</button>
              </li>
            </ul>
          </div>
        </div>
        <div v-else>
          <p class="no-results">There are currently no contacts</p>
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
