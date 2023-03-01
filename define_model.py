import torch
from torch import nn
import numpy as np

# X = np.arange(300)

# X = torch.from_numpy(X).float()

# dense1 = nn.Linear(300,10)
# print(dense1(X))


class MLP(nn.Module):
    def __init__(self):
        super().__init__()
        self.linear_relu_stack = nn.Sequential(
            nn.Linear(300, 10),
            nn.ReLU(),
            nn.Linear(10, 1),
            nn.Tanh()
        )

    def forward(self, x):
        logits = self.linear_relu_stack(x)
        return logits


model = MLP()

# torch.save(model.state_dict(), "model.pth")


# Save the model for Android
from torch.utils.mobile_optimizer import optimize_for_mobile

example = torch.rand(300)
traced_script_module = torch.jit.trace(model, example)
optimized_traced_model = optimize_for_mobile(traced_script_module)
# optimized_traced_model._save_for_lite_interpreter("app/src/main/assets/model.pt")
optimized_traced_model._save_for_lite_interpreter("./model.pt")

