import matplotlib.pyplot as plt

ySizes = [50, 100, 250, 500, 1000, 5000]

x1, y1 = [], []
x2, y2 = [], []
x3, y3 = [], []
x4, y4 = [], []
x5, y5 = [], []
x6, y6 = [], []
x7, y7 = [], []
x8, y8 = [], []
x9, y9 = [], []
x10, y10 = [], []
x1.append(0), y1.append(0)
x2.append(0), y2.append(0)
x3.append(0), y3.append(0)
x4.append(0), y4.append(0)
x5.append(0), y5.append(0)
x6.append(0), y6.append(0)
x7.append(0), y7.append(0)
x8.append(0), y8.append(0)
x9.append(0), y9.append(0)
x10.append(0), y10.append(0)

def getySize (max):
    size = max;
    for i in range(0, len(ySizes)):
        if max < ySizes[i]:
            return ySizes[i]
        else:
            continue
    return -1



def fillPoints (x, y, ySize):
    xs = []
    ycounter = 1
    filluntil = y[ycounter]

    for i in range(0, ySize):
        if i >= filluntil:
            ycounter += 1
            if ycounter >= len(y):
                filluntil = ySize
            else:
                filluntil = y[ycounter]

        xs.append(x[ycounter - 1])

    # Remove first element and add last to the back since the errors are not 0 indexed
    xs.remove(0)
    xs.append(xs[len(xs)-1])

    return xs

for line in open('../../output/MovieRating-10runs-invalid/1623502334052/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x10.append(values[1])
    y10.append(values[0])

for line in open('../../output/MovieRating-10runs-invalid/1623502347403/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x9.append(values[1])
    y9.append(values[0])

for line in open('../../output/MovieRating-10runs-invalid/1623502359731/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x8.append(values[1])
    y8.append(values[0])

for line in open('../../output/MovieRating-10runs-invalid/1623502369938/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x7.append(values[1])
    y7.append(values[0])

for line in open('../../output/MovieRating-10runs-invalid/1623502379484/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x6.append(values[1])
    y6.append(values[0])

for line in open('../../output/MovieRating-10runs-invalid/1623502391394/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x5.append(values[1])
    y5.append(values[0])

for line in open('../../output/MovieRating-10runs-invalid/1623502403326/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x4.append(values[1])
    y4.append(values[0])

for line in open('../../output/MovieRating-10runs-invalid/1623502413039/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x3.append(values[1])
    y3.append(values[0])

for line in open('../../output/MovieRating-10runs-invalid/1623502424240/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x2.append(values[1])
    y2.append(values[0])

for line in open('../../output/MovieRating-10runs-invalid/1623502434897/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x1.append(values[1])
    y1.append(values[0])

rangeSize = max(y1[len(y1)-1], y2[len(y2)-1], y3[len(y3)-1], y4[len(y4)-1], y5[len(y5)-1], y6[len(y6)-1], y7[len(y7)-1],
                y8[len(y8)-1], y9[len(y9)-1], y10[len(y10)-1])

ySize = getySize(rangeSize)
print(ySize)

x1 = fillPoints(x1, y1, ySize)
x2 = fillPoints(x2, y2, ySize)
x3 = fillPoints(x3, y3, ySize)
x4 = fillPoints(x4, y4, ySize)
x5 = fillPoints(x5, y5, ySize)
x6 = fillPoints(x6, y6, ySize)
x7 = fillPoints(x7, y7, ySize)
x8 = fillPoints(x8, y8, ySize)
x9 = fillPoints(x9, y9, ySize)
x10 = fillPoints(x10, y10, ySize)
ys = range(ySize)

xaverage = []
for i in range(0, ySize):
    avg_value = (x1[i] + x2[i] + x3[i] + x4[i] + x5[i] + x6[i] + x7[i] + x8[i] + x9[i] + x10[i]) / 10.0
    xaverage.append(avg_value)

print(xaverage)

plt.plot(ys, xaverage, label="Average of 10 runs", color="black")
plt.xlabel("Number of trials")
plt.ylabel("Unique errors found")
plt.legend(loc="lower right")
plt.show()

