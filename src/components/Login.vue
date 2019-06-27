<template>
    <div id="login">
        <transition name="fade">
            <div v-if="performingRequest" class="loading">
                <p>Loading...</p>
            </div>
        </transition>
        <section>
            <div class="col1">
              <img src="../assets/images/ucflogolarge.png"/>
              <h1>Knights ComeTogether</h1>
            </div>
            <div class="col2">
                <form v-if="showLoginForm" @submit.prevent>
                    <h1>Welcome Back</h1>

                    <label for="email1">Email</label>
                    <input v-model.trim="loginForm.email" type="text" placeholder="you@email.com" id="email1" />

                    <label for="password1">Password</label>
                    <input v-model.trim="loginForm.password" type="password" placeholder="******" id="password1" />

                    <button @click="login" class="button">Log In</button>

                    <div class="extras">
                        <a @click="toggleForm">Create an Account</a>
                    </div>
                </form>
               <!--Pick what type of user you are
                    form will change depending on user type
                    THIS SWAP FEATURE DOES NOT WORK PROPERLY-->
                <form v-if="!showLoginForm" @submit.prevent>
                  <h1>Get Started</h1>
                  <input type="radio" v-model.trim="signupForm.accountType" value="Student" @click="toggleFormType" checked> I am a Student<br>
                  <input type="radio" v-model.trim="signupForm.accountType" value="Organization" @click="toggleFormType"> I am an Organization<br>
                </form>
                 <!--Student Signup-->
                <form v-if="!showLoginForm && showStudentForm" @submit.prevent>
                    <label for="firstName">First Name</label>
                    <input v-model.trim="signupForm.firstName" type="text" placeholder="First Name" id="name" />

                    <label for="lastName">Last Name</label>
                    <input v-model.trim="signupForm.lastName" type="text" placeholder="Last Name" id="name" />

                    <label for="email2">Email</label>
                    <input v-model.trim="signupForm.email" type="text" placeholder="Email" id="email2" />

                    <label for="password2">Password</label>
                    <input v-model.trim="signupForm.password" type="password" placeholder="min 6 characters" id="password2" />

                    <button @click="signup" class="button">Sign Up</button>
                </form>
                <!--Organization Signup-->
                <form v-if="!showLoginForm && !showStudentForm" @submit.prevent>
                    <label for="organizationName">Organization Name</label>
                    <input v-model.trim="signupForm.organizationName" type="text" placeholder="Organization Name" id="name" />

                    <label for="organizationDetails">Organization Details</label>
                    <input v-model.trim="signupForm.organizationDetails" type="text" placeholder="Details" id="details" />

                    <label for="email2">Email</label>
                    <input v-model.trim="signupForm.email" type="text" placeholder="Email" id="email2" />

                    <label for="password2">Password</label>
                    <input v-model.trim="signupForm.password" type="password" placeholder="min 6 characters" id="password2" />

                    <button @click="signup" class="button">Sign Up</button>
                </form>
                <div v-if="!showLoginForm" class="extras">
                      <a @click="toggleForm">Back to Log In</a><br>
                </div>
            </div>
        </section>
    </div>
</template>

<script>
const fb = require('../../firebaseConfig.js')

    export default {
        data() {
            return {
                loginForm: {
                    email: '',
                    password: ''
                },
                signupForm: {
                    accountType: '',
                    firstName: '',
                    lastName: '',
                    organizationName: '',
                    organizationDetails: '',
                    email: '',
                    password: ''
                },
                showLoginForm: true,
                performingRequest: false,
                showStudentForm: true,
                showOrganizationForm: false
            }
        },
        methods: {
            toggleForm() {
                this.showLoginForm = !this.showLoginForm
            },
            toggleFormType() {
                this.showStudentForm = !this.showStudentForm;
            },
            login() {
                this.performingRequest = true

                fb.auth.signInWithEmailAndPassword(this.loginForm.email, this.loginForm.password).then(user => {
                    this.$store.commit('setCurrentUser', user.user)
                    this.$store.dispatch('fetchUserProfile')
                    this.performingRequest = false
                    this.$router.push('/dashboard')
                }).catch(err => {
                    console.log(err)
                    this.performingRequest = false
                })
            },
            signup() {
                this.performingRequest = true
                fb.auth.createUserWithEmailAndPassword(this.signupForm.email, this.signupForm.password).then(user => {
                    this.$store.commit('setCurrentUser', user.user)

                    // create user obj
                    fb.usersCollection.doc(user.user.uid).set({
                        firstName: this.signupForm.firstName,
                        lastName: this.signupForm.lastName,
                        accountType: this.signupForm.accountType,
                        organizationName: this.signupForm.organizationName,
                        organizationDetails: this.signupForm.organizationDetails
                    }).then(() => {
                        this.$store.dispatch('fetchUserProfile')
                        this.performingRequest = false
                        this.$router.push('/dashboard')
                    }).catch(err => {
                        console.log(err)
                        this.performingRequest = false
                    })
                }).catch(err => {
                    console.log(err)
                })
            }
        }
    }
</script>
