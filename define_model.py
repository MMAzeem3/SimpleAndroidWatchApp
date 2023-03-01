import torch
from torch import nn
import numpy as np

class MLP(nn.Module):
    def __init__(self):
        super().__init__()
        self.linear_relu_stack = nn.Sequential(
            nn.Linear(300, 10),
            nn.ReLU(),
            nn.Linear(10, 1),
        )

    def forward(self, x):
        logits = self.linear_relu_stack(x)
        return logits


model = MLP()
example = torch.full((300,), 2.0)
print(model(example))

# Save the model for Android
from torch.utils.mobile_optimizer import optimize_for_mobile

# example = torch.rand(300)
traced_script_module = torch.jit.trace(model, example)
optimized_traced_model = optimize_for_mobile(traced_script_module)
optimized_traced_model._save_for_lite_interpreter("WearOSPytorchTemplate/app/src/main/assets/model.pt")

