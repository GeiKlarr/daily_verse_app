<template>
  <div class="bg-gradient-to-br from-indigo-100 via-white to-blue-50 shadow-xl rounded-3xl p-8 max-w-lg text-center border border-indigo-200">
    <h1 class="text-3xl font-extrabold text-indigo-700 mb-6">
      Daily Bible Verse
    </h1>

    <div v-if="loading" class="animate-pulse text-gray-500">
      ⏳ Loading verse...
    </div>

    <div v-else-if="error" class="text-red-600 font-semibold">
      ⚠️ {{ error }}
    </div>

    <div v-else>
      <blockquote class="italic text-xl text-gray-800 leading-relaxed mb-4">
        “{{ verseInstant.value.text || verse.text }}”
      </blockquote>
      <p class="text-indigo-600 font-semibold text-lg">
        — {{ verse.value.reference || verse.value.reference }}
      </p> 
    </div>

    <!-- Collapse button (only show if sermon is available) -->
    <button v-if="verse.sermon_url" @click="showExtra = !showExtra" class="mt-8 px-6 py-3 bg-indigo-600 text-white font-semibold rounded-full shadow-md hover:bg-indigo-700 hover:shadow-lg transition-all duration-300 ease-in-out">
      {{ showExtra ? "⬆️ Hide Details" : "⬇️ Show Details" }}
    </button>

    <transition name="fade">
      <div v-if="showExtra" class="mt-6 p-6 bg-indigo-50 rounded-2xl shadow-lg space-y-4 border border-indigo-200">
        <p class="text-gray-800">
          <span class="font-semibold">Sermon Author:</span> {{ verse.sermon_author || "N/A" }}
        </p>
        <p class="text-gray-800">
          <span class="font-semibold">Publisher:</span> {{ verse.sermon_publisher || "N/A" }}
        </p>

        <div>
          <span class="font-semibold text-gray-800">Watch Sermon:</span>

          <div v-if="verse.sermon_url" class="mt-3 w-full h-96 md:h-[500px] lg:h-[600px] rounded-xl overflow-hidden shadow-md border border-indigo-300">
            <iframe class="w-full h-full" :src="getYoutubeEmbedUrl(verse.sermon_url)" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
          </div>

          <p v-else class="mt-3 text-gray-600 italic bg-white p-4 rounded-lg border border-gray-200 shadow-inner">
            ⚠️ Sorry, no sermon is available for this verse.
          </p>
        </div>
      </div>
    </transition>


  </div>
</template>


<script setup>
import { ref, onMounted } from "vue";

const verse = ref({});
const verseInstant = ref({});
const loading = ref(true);
const error = ref(null);
const showExtra = ref(false);

const url = "https://beta.ourmanna.com/api/v1/get?format=json&order=daily";

async function getVerse() {
  loading.value = true;
  error.value = null;

  try {
    const res = await fetch(url, {
      method: "GET",
      headers: { accept: "application/json" },
    });
    const json = await res.json();
    verse.value = {
      text: json.verse.details.text,
      reference: json.verse.details.reference,
    };

    verseInstant.value = { 
      text: json.verse.details.text,
      reference: json.verse.details.reference,
     };

    await sendVerseToLocalApi(verse.value.reference);
  } catch (err) {
    error.value = err.message || "Failed to load verse.";
  } finally {
    loading.value = false;
  }
}

async function sendVerseToLocalApi(reference) {
  try {
    const res = await fetch("https://daily-verse-app-amqy.onrender.com/api/verse/get", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ scripture: reference }),
    });
    if (!res.ok) throw new Error("Failed to send verse to local API");

    const data = await res.json();
    console.log("📥 Local API response:", data);

    verse.value = { ...verse.value, ...data };

  } catch (err) {
    console.error(err);
    error.value = err.message || "Failed to call local API.";
  }
}

function getYoutubeEmbedUrl(url) {
  if (!url) return "";
  const videoId = url.split("v=")[1];
  if (!videoId) return "";
  return `https://www.youtube.com/embed/${videoId}`;
}



onMounted(() => {
  getVerse();
});
</script>

<style scoped>
/* Optional subtle fade-in for the verse */
blockquote {
  animation: fadeIn 1s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.4s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
