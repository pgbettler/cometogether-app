<template>
<div>
<div class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">

          <div class="modal-header">
            <slot name="header">
              Upload an Image
            </slot>
          </div>

          <div class="modal-body">
            <slot name="body">
              <button raised id="UploadBTN" @click="chooseOnClick">Choose Image</button>
              <input type="file"
               style="display: none" 
               accept="image/*" 
               ref="fileInput"
               @click="onPickedImage">
            </slot>
          </div>

          <div class="modal-footer">
            
              <!--<img: src="imageUrl" height="150" > -->
             
              <slot name="footer">
              <button class="button" v-on:click="hideUpload">
                OK
              </button>
              </slot>

          </div>
        </div>
      </div>
    </div>
    </div>

</template>

<!-- Javascript -->
<script>


export default {
data() {
  return {
  imageUrl: '',
  showUploadForm: '',
  image : null
          }
      },

methods: {
    hideUpload() {
      this.$emit('childCall', false)
    },
    chooseOnClick() {
        this.$refs.fileInput.click()
    },
    onPickedImage(event) {
        const files = event.target.files
        let filename = files[0].name
        if (filename.lastIndexOf('.') <= 0) {
            return alert('Please upload a valid file.')
        }
        const fileReader = new fileReader()
        fileReader.addEventListener('load', () => {
            this.imageUrl = fileReader.result
        })
        fileReader.readAsDataURL(files[0])
        this.image = files[0]
    }
},
}

</script>

<!-- SCSS Styling -->
<style>
.modal-mask {
  position: fixed;
  z-index: 9998;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, .5);
  display: table;
  transition: opacity .3s ease;
}

.modal-wrapper {
  display: table-cell;
  vertical-align: middle;
}

.modal-container {
  width: 300px;
  margin: 0px auto;
  padding: 20px 30px;
  background-color: #fff;
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, .33);
  transition: all .3s ease;
  font-family: Helvetica, Arial, sans-serif;
}

.modal-header h3 {
  margin-top: 0;
  color: #42b983;
}

.modal-body {
  margin: 20px 0;
}

.modal-default-button {
  float: right;
}

/*
 * The following styles are auto-applied to elements with
 * transition="modal" when their visibility is toggled
 * by Vue.js.
 *
 * You can easily play with the modal transition by editing
 * these styles.
 */

.modal-enter {
  opacity: 0;
}

.modal-leave-active {
  opacity: 0;
}

.modal-enter .modal-container,
.modal-leave-active .modal-container {
  -webkit-transform: scale(1.1);
  transform: scale(1.1);
}
</style>

