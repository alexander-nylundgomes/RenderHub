/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      aspectRatio: {
        "file-card": "3 / 4"
      },
      colors: {
        'danger': '#dc2626'
      }
    },
  },
  plugins: [],
}

