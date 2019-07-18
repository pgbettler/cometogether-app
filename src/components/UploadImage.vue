<template>
 <div class= 'Image'>
     <container>
     <p> Upload an Image </p>
     <button small raised @click= "onPickFile" id='uploadBTN'> Choose File </button>
     <input type="file" style="display: none" ref="fileInput" accept="image/*">
 </container>
 </div>
</template>

<!-- Javascript -->
<script>


export default {
methods: {
    onPickFile() {
        this.$refs.fileInput.click();
    }
}
}

</script>

<!-- SCSS Styling -->
<style lang="scss" scoped>
.button {
	border: 0;
	outline: 0;
	color: white;
	padding: 0.8rem 1rem;
	min-width: 150px;
	font-size: 16px;
	border-radius: 3px;
}

</style>
 

