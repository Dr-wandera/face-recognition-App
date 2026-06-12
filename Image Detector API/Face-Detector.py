from flask import Flask, request, jsonify
import base64
import numpy as np
import cv2

import tensorflow as tf
from tensorflow.keras import mixed_precision

# This  allocate GPU memory to prevent initial bottleneck slowdowns
gpus = tf.config.list_physical_devices('GPU')
if gpus:
    for gpu in gpus:
        tf.config.experimental.set_memory_growth(gpu, True)

# 2. Enforce Mixed Precision (FP16/Int8 policy) globally to accelerate inference
try:
    policy = mixed_precision.Policy('mixed_float16')
    mixed_precision.set_global_policy(policy)
    print("[INIT] TensorFlow FP16 Mixed Precision enabled successfully.")
except Exception as e:
    print(f"[INIT WARNING] Mixed precision configuration skipped: {e}")

from deepface import DeepFace


app = Flask(__name__)

#  using two independent model, top-tier architectures to cross-verify the face. If both model return true, then return granted
PRIMARY_MODEL = "Facenet512"
SECONDARY_MODEL = "ArcFace"
DETECTOR = "retinaface"
DISTANCE_METRIC = "cosine"

# Aggressively low thresholds. Standard defaults are 0.40.
# Dropping these to ~0.22 means the structural vectors must be near-identical.
PRIMARY_THRESHOLD = 0.22
SECONDARY_THRESHOLD = 0.25

@app.route("/")
def home():
    return "Ultra-Secure Student Verification API running"

def base64_to_image(base64_string):
    if not base64_string:
        raise ValueError("Image is missing")
    try:
        if "," in base64_string:
            base64_string = base64_string.split(",")[1]
        img_data = base64.b64decode(base64_string)
        np_arr = np.frombuffer(img_data, np.uint8)
        image = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)
        if image is None:
            raise ValueError("Failed to decode image")
        return image.astype(np.uint8)
    except Exception as e:
        raise ValueError(f"Invalid image data: {str(e)}")

@app.route('/compare', methods=['POST'])
def compare():
    try:
        data = request.get_json()
        if not data or 'registeredImage' not in data or 'newImage' not in data:
            return jsonify({"error": "Missing image payload"}), 400

        # Convert payloads to OpenCV images
        registered_img = base64_to_image(data['registeredImage'])
        new_img = base64_to_image(data['newImage'])

        # LAYER 1: Primary Verification (Facenet512)
        result1 = DeepFace.verify(
            img1_path = registered_img,
            img2_path = new_img,
            model_name = PRIMARY_MODEL,
            detector_backend = DETECTOR,
            distance_metric = DISTANCE_METRIC,
            enforce_detection = True,
            align = True
        )
        dist1 = float(result1["distance"])
        match1 = dist1 < PRIMARY_THRESHOLD

        # LAYER 2: Secondary Cross-Check (ArcFace)
        result2 = DeepFace.verify(
            img1_path = registered_img,
            img2_path = new_img,
            model_name = SECONDARY_MODEL,
            detector_backend = DETECTOR,
            distance_metric = DISTANCE_METRIC,
            enforce_detection = True,
            align = True
        )
        dist2 = float(result2["distance"])
        match2 = dist2 < SECONDARY_THRESHOLD

        # ENSEMBLE GATE: Both systems must independently pass
        is_strictly_verified = match1 and match2

        print(
            f"[SECURITY LOG]\n"
            f"  -> {PRIMARY_MODEL} Dist: {dist1:.4f} (Req: <{PRIMARY_THRESHOLD}) | Match: {match1}\n"
            f"  -> {SECONDARY_MODEL} Dist: {dist2:.4f} (Req: <{SECONDARY_THRESHOLD}) | Match: {match2}\n"
            f"  -> FINAL DECISION: {'GRANTED' if is_strictly_verified else 'DENIED'}"
        )

        return jsonify({
            "status": "GRANTED" if is_strictly_verified else "DENIED",
            "match": is_strictly_verified,
            "details": {
                f"{PRIMARY_MODEL}_distance": dist1,
                f"{SECONDARY_MODEL}_distance": dist2
            }
        })

    except Exception as e:
        return jsonify({"status": "ERROR", "error": str(e)}), 500

if __name__ == '__main__':
    # downloads model on flasks initialization time
    print(" Pre-loading face models into cache... Please wait.")
    try:
        # This  securely download ArcFace and Facenet512 before starting the server
        DeepFace.build_model("Facenet512")
        DeepFace.build_model("ArcFace")
        print("[INIT] Models loaded successfully!")
    except Exception as e:
        print(f"[INIT WARNING] Model pre-warm failed or skipped: {e}")

    # Start your API server at port 5000
    app.run(port=5000, debug=True)
