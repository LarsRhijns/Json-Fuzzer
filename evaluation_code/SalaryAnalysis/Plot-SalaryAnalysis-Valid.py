import matplotlib.pyplot as plt

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


def fillPoints (x, y):
    xs = []
    ycounter = 1
    filluntil = y[ycounter]

    for i in range(0,50):
        if i >= filluntil:
            ycounter += 1
            if ycounter >= len(y):
                filluntil = 50
            else:
                filluntil = y[ycounter]

        xs.append(x[ycounter - 1])

    # Remove first element and add last to the back since the errors are not 0 indexed
    xs.remove(0)
    xs.append(xs[len(xs)-1])

    return xs

for line in open('../../output/SalaryAnalysis-10runs-valid/1623055143042/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x10.append(values[1])
    y10.append(values[0])

for line in open('../../output/SalaryAnalysis-10runs-valid/1623055225322/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x9.append(values[1])
    y9.append(values[0])

for line in open('../../output/SalaryAnalysis-10runs-valid/1623055269878/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x8.append(values[1])
    y8.append(values[0])

for line in open('../../output/SalaryAnalysis-10runs-valid/1623055314598/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x7.append(values[1])
    y7.append(values[0])

for line in open('../../output/SalaryAnalysis-10runs-valid/1623055368682/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x6.append(values[1])
    y6.append(values[0])

for line in open('../../output/SalaryAnalysis-10runs-valid/1623055411177/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x5.append(values[1])
    y5.append(values[0])

for line in open('../../output/SalaryAnalysis-10runs-valid/1623055462545/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x4.append(values[1])
    y4.append(values[0])

for line in open('../../output/SalaryAnalysis-10runs-valid/1623055519968/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x3.append(values[1])
    y3.append(values[0])

for line in open('../../output/SalaryAnalysis-10runs-valid/1623055565194/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x2.append(values[1])
    y2.append(values[0])

for line in open('../../output/SalaryAnalysis-10runs-valid/1623055646240/datapoints/points.txt', 'r'):
    values = [float(s) for s in line.split(',')]
    x1.append(values[1])
    y1.append(values[0])

x1 = fillPoints(x1, y1)
x2 = fillPoints(x2, y2)
x3 = fillPoints(x3, y3)
x4 = fillPoints(x4, y4)
x5 = fillPoints(x5, y5)
x6 = fillPoints(x6, y6)
x7 = fillPoints(x7, y7)
x8 = fillPoints(x8, y8)
x9 = fillPoints(x9, y9)
x10 = fillPoints(x10, y10)
ys = range(50)

xaverage = []
for i in range(0, 50):
    avg_value = (x1[i] + x2[i] + x3[i] + x4[i] + x5[i] + x6[i] + x7[i] + x8[i] + x9[i] + x10[i]) / 10.0
    xaverage.append(avg_value)

print(xaverage)

plt.plot(ys, xaverage, label="Average of 10 runs", color="black")
plt.xlabel("Number of trials")
plt.ylabel("Unique errors found")
plt.legend(loc="lower right")
plt.show()

