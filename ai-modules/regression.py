from sklearn.model_selection import TimeSeriesSplit, cross_val_score
from sklearn.linear_model import LinearRegression
import numpy as np

features = ['lag_1', 'lag_2', 'lag_3', 'rolling_mean_3', 'month_num']
X = monthly[features]
y = monthly['total_expense']

tscv = TimeSeriesSplit(n_splits=5)
model = LinearRegression()
scores = cross_val_score(model, X, y, cv=tscv, scoring='r2')
print(f"R² per fold : {scores.round(3)}")
print(f"Mean R²     : {scores.mean():.4f}")
print(f"Std Dev     : {scores.std():.4f}")