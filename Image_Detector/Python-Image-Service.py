from flask import Flask, request, jsonify
import base64
import numpy as np
import cv2
from deepface import DeepFace

app = Flask(__name__)

# config for flask
MODEL_NAME = "Facenet512"     # More accurate model
DETECTOR = "mtcnn"            # Better face detection
THRESHOLD = 0.35              # Strict matching threshold

# test if flask is up and running
@app.route("/")
def home():
    return "Student Verification System API running (optimized & accurate)"

# Convert base64 to image
def base64_to_image(base64_string):
    if not base64_string:
        raise ValueError("Image is missing")

    try:
        # remove header if exists
        if "," in base64_string:
            base64_string = base64_string.split(",")[1]

        img_data = base64.b64decode(base64_string)
        np_arr = np.frombuffer(img_data, np.uint8)
        image = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

        if image is None:
            raise ValueError("Failed to decode image")

        # Resize image for speed + consistency
        image = cv2.resize(image, (320, 320))

        return image

    except Exception as e:
        raise ValueError(f"Invalid image data: {str(e)}")

# compare face
@app.route('/compare', methods=['POST'])
def compare():
    try:
        data = request.get_json()

        if not data:
            return jsonify({"error": "Request body missing"}), 400

        if 'registeredImage' not in data or 'newImage' not in data:
            return jsonify({"error": "Missing images"}), 400

        # Convert to images
        registered_img = base64_to_image(data['registeredImage'])
        new_img = base64_to_image(data['newImage'])

        # Method for verification
        result = DeepFace.verify(
            img1_path=registered_img,
            img2_path=new_img,
            model_name=MODEL_NAME,
            detector_backend=DETECTOR,
            enforce_detection=True
        )

        distance = float(result["distance"])
        is_match = distance < THRESHOLD

        # log info for debugging
        print(
            f"[DEBUG] Distance: {distance} | "
            f"Threshold: {THRESHOLD} | "
            f"Match: {is_match}"
        )

        return jsonify({
            "status": "GRANTED" if is_match else "DENIED",
            "match": is_match,
            "distance": distance,
            "threshold": THRESHOLD
        })

    except Exception as e:
        return jsonify({
            "status": "ERROR",
            "error": str(e)
        }), 500

# Running server
if __name__ == '__main__':
    app.run(port=5000, debug=True)