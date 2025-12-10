# Implementation Guide: Multi-Modal Sentiment Analysis

## 1. Prerequisites & Installation

First, let's install the necessary libraries for tensor computation and deep learning.

```bash
pip install torch torchvision torchaudio librosa
```

---

## 2. Data Processing

### Handling Batch Dimensions

When processing individual samples, we often need to remove the batch dimension added by tokenizers.

```python
return {
    'text_input': {
        # Squeeze to remove batch dimension (1, seq_len) -> (seq_len,)
        'input_ids': text_input['input_ids'].squeeze(),
        'attention_mask': text_input['attention_mask'].squeeze()
    },
    'video_frames': video_frames,
}
```

**Why Squeeze?**
- Tokenizers output tensors with shape `(1, sequence_length)` to handle batch processing.
- For a single sample in a `Dataset` class, we need `(sequence_length,)`.
- The `DataLoader` will later add the batch dimension back, creating `(batch_size, sequence_length)`.

### Visual Representation

**Before Squeeze:** `(1, 128)`
```
[[101, 123, 456, ...]]  <-- Batch dimension 0 has size 1
```

**After Squeeze:** `(128,)`
```
[101, 123, 456, ...]    <-- No batch dimension, just the sequence
```

### Dataset Class Structure

We define a custom `Dataset` class with the following key methods:

1.  `__init__`: Initialize dataset, load metadata, set up tokenizer and transforms.
2.  `_load_video_frames`: Load and preprocess video frames from a file path.
3.  `_extract_audio_features`: Extract audio spectrograms or features.
4.  `__len__`: Return the total number of samples.
5.  `__getitem__`: Return a single data point (video, text, audio, label) at a specific index.

---

## 3. Model Architecture

Our model fuses information from three encoders:

1.  **Video Encoder:** ResNet3D (18 layers) -> Extracts spatial-temporal features.
2.  **Audio Encoder:** Raw Spectrogram + CNN -> Extracts acoustic features.
3.  **Text Encoder:** BERT Base Uncased -> Extracts semantic meaning.

**Fusion Strategy:**
- Outputs from all 3 encoders are concatenated into a single tensor.
- This combined tensor is passed to a **Fusion Layer**.
- The Fusion Layer learns relationships between modalities.
- Finally, the output goes to **Emotion** (7 classes) and **Sentiment** (3 classes) classifiers.

---

## 4. Neural Network Layers Explained

### Trainable Layers (Learn from data)
*   **Linear (Fully Connected):** Connects every input neuron to every output neuron. Great for classification.
*   **Conv1D:** Convolutional layer for 1D sequences (like audio). Learns local patterns using filters.

### Functional Layers (Operations)
*   **ReLU (Rectified Linear Unit):** Activation function. Outputs `max(0, x)`. Introduces non-linearity.
*   **Dropout:** Randomly zeroes out neurons during training to prevent overfitting.
*   **MaxPool1D:** Downsampling. Takes the maximum value in a window. Reduces dimensionality.
*   **AdaptiveAvgPool1D:** Downsampling. Outputs a fixed size regardless of input length.

### Normalization Layers (Stability)
*   **BatchNorm1D:** Normalizes inputs across the batch. Stabilizes and speeds up training.

---

## 5. Training Workflow

### Data Flow by Modality

1.  **Video:**
    *   Input: `[batch_size, channels, depth, height, width]`
    *   Process: ResNet3D -> Linear (reduce to 128) -> ReLU -> Dropout.

2.  **Text:**
    *   Input: `input_ids`, `attention_mask`
    *   Process: BERT -> Pooled Output (768) -> Linear (reduce to 128).

3.  **Audio:**
    *   Input: Spectrogram (64 features)
    *   Process: Conv1D -> BatchNorm -> ReLU -> MaxPool -> Conv1D -> BatchNorm -> ReLU -> AdaptiveAvgPool -> Linear (reduce to 128) -> ReLU -> Dropout.

### Fusion & Classification
*   **Concatenation:** Combine features `[batch_size, 128 * 3]`.
*   **Fusion Layer:** Learn cross-modal relations.
*   **Classifiers:** Output predictions for Emotion and Sentiment.

---

## 6. Training Configuration

*   **Optimizer:** `Adam` (Learning Rate: 1e-4).
    *   Combines AdaGrad and RMSProp. Adapts learning rates for each parameter. Good for noisy/sparse data.
*   **Loss Function:** `CrossEntropyLoss`.
    *   Standard for multi-class classification. Measures difference between predicted and actual probability distributions.
*   **Scheduler:** `ReduceLROnPlateau`.
    *   Reduces learning rate if validation loss stops improving. Helps converge to global minima.

### Advanced Concepts

*   **`nn.Sequential`:** A container that stacks layers in order. Simplifies model definition.
*   **Gradient Clipping:** Prevents "exploding gradients" by capping them at a threshold. Crucial for deep networks or RNNs.

### Parameter Counting

Understanding model size is vital for resource planning and preventing overfitting.

```bash
Trainable parameters:
- Text Encoder:       98,432
- Video Encoder:      65,664
- Audio Encoder:      16,512
- Fusion Layer:       99,072
- Emotion Classifier: 16,903
- Sentiment Classifier: 16,643
```

*   **Trainable:** Updated during backpropagation (Weights, Biases).
*   **Untrainable:** Fixed during training (Pre-trained embeddings, frozen layers).

---

## 7. AWS SageMaker Infrastructure

To train this model at scale, we use Amazon SageMaker.

### Key Components
*   **EC2 Instance:** `ml.g5.large` (GPU-enabled) for fast training.
*   **S3 Bucket:** Stores the dataset and training artifacts. Needs CORS setup for web access.
*   **IAM Role:** Permissions for SageMaker to access S3 and other services.
*   **SageMaker Notebook:** Managed Jupyter environment for development.

### PyTorch Estimator
A high-level SageMaker SDK class that handles the complexity of setting up PyTorch training environments. It manages the underlying EC2 instances and Docker containers.

### Training Job Flow
1.  **Script:** `train_sagemaker.py` prepares the job.
2.  **Upload:** Code and data are uploaded to S3.
3.  **Build:** SageMaker launches a Docker container with PyTorch.
4.  **Execute:** `train.py` runs inside the container on the managed infrastructure.
