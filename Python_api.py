# Flask library ko import karte hain aur translation ke liye deep_translator ka use karte hain.
from flask import Flask, request, jsonify
from deep_translator import GoogleTranslator

# Yeh Flask app initialize kar raha hai.
app = Flask(__name__)

# Yeh list Indian languages ke codes ko define karne ke liye hai.
indian_languages = [
    'hi',  # Hindi
    'bn',  # Bengali
    'te',  # Telugu
    'mr',  # Marathi
    'ta',  # Tamil
    'ur',  # Urdu
    'gu',  # Gujarati
    'ml',  # Malayalam
    'kn',  # Kannada
    'pa',  # Punjabi
    'as',  # Assamese
    'or'   # Oriya
]

# Translation ke liye API route banaya gaya hai.
@app.route('/translate', methods=['GET'])
def translate():
    # Yeh user ke input text aur destination language ko request se get karta hai.
    text = request.args.get('text')
    dest_language = request.args.get('dest_language')

    # Debugging ke liye user ke inputs ko print karte hain.
    print(f"Received text: {text}, Destination language: {dest_language}")

    # Yeh check karta hai ki text aur destination language sahi se diye gaye hain ya nahi.
    if not text or not dest_language:
        return jsonify({'error': 'Text and destination language are required'}), 400

    # Yeh check karta hai ki destination language supported hai ya nahi.
    if dest_language not in indian_languages:
        return jsonify({'error': 'Invalid destination language code'}), 400

    try:
        # Deep Translator ka use karke text translate kiya ja raha hai.
        translated_text = GoogleTranslator(source='auto', target=dest_language).translate(text)
        return jsonify({'translated_text': translated_text})
    except Exception as e:
        # Error ko log karte hain aur user ko message return karte hain.
        print(f"Translation error: {e}")
        return jsonify({'error': 'Failed to translate'}), 500

# Flask app ko debug mode mein run karne ke liye.
if __name__ == '__main__':
    app.run(debug=True)
