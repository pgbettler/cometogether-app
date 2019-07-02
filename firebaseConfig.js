import firebase from 'firebase'
import 'firebase/firestore'

const config = {
  apiKey: "AIzaSyCq-u-Sb_F-JSgFivV5niLDBGvMY9L5-70",
  authDomain: "cometogether-app.firebaseapp.com",
  databaseURL: "https://cometogether-app.firebaseio.com",
  projectId: "cometogether-app",
  storageBucket: "cometogether-app.appspot.com",
  messagingSenderId: "109482956505",
  appId: "1:109482956505:web:bcbf1f62a92b0aa3"
};
firebase.initializeApp(config)

// firebase utils
const db = firebase.firestore()
const auth = firebase.auth()
const currentUser = auth.currentUser

// date issue fix according to firebase
const settings = {
    timestampsInSnapshots: true
}
db.settings(settings)

// firebase collections
const usersCollection = db.collection("users")
const postsCollection = db.collection("posts")
const likesCollection = db.collection("likes")
const followsCollection = db.collection("follows")
const organizationsCollection = db.collection("organizations")

export {
    db,
    auth,
    currentUser,
    usersCollection,
    postsCollection,
    likesCollection,
    followsCollection,
    organizationsCollection
}
