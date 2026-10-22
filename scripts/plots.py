import csv
import math
import sys
from collections import defaultdict
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt
import numpy as np

CSV_PATH = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("results/results.csv")
PLOT_DIR = Path(sys.argv[2]) if len(sys.argv) > 2 else Path("results/plots")
ANALYSIS_PATH = PLOT_DIR.parent / "analysis.md"

ALGORITHMS = ["Kruskal", "Prim", "Kosaraju", "Kahn", "DagShortestPath"]
DENSITIES = ["sparse", "dense"]
COLORS = {
    "Kruskal": "#1f77b4",
    "Prim": "#ff7f0e",
    "Kosaraju": "#2ca02c",
    "Kahn": "#d62728",
    "DagShortestPath": "#9467bd",
}
STYLES = {"sparse": "-", "dense": "--"}
MARKERS = {"sparse": "o", "dense": "s"}
HALO = {"Prim", "Kahn"}


def line_kwargs(alg, d):
    if alg in HALO:
        return dict(linestyle=STYLES[d], marker=MARKERS[d], color=COLORS[alg],
                    linewidth=7, markersize=11, alpha=0.4, zorder=1)
    return dict(linestyle=STYLES[d], marker=MARKERS[d], color=COLORS[alg],
                linewidth=2, markersize=5, zorder=2)


def load():
    data = defaultdict(list)
    with open(CSV_PATH, newline="") as f:
        for row in csv.DictReader(f):
            density = row["graph"].split("_")[1]
            data[(row["algorithm"], density)].append({
                "n": int(row["n"]),
                "m": int(row["m"]),
                "time_ms": float(row["time_ms"]),
                "dsu_ops": int(row["dsu_ops"]),
                "edge_scans": int(row["edge_scans"]),
                "comparisons": int(row["comparisons"]),
                "max_depth": int(row["max_depth"]),
                "result": int(row["result"]),
            })
    for key in data:
        data[key].sort(key=lambda r: r["n"])
    return data


def slope(xs, ys):
    return float(np.polyfit(np.log10(xs), np.log10(ys), 1)[0])


def style(ax, xlabel, ylabel, title):
    ax.set_xlabel(xlabel)
    ax.set_ylabel(ylabel)
    ax.set_title(title)
    ax.grid(True, which="both", alpha=0.3)
    ax.legend(fontsize=8, ncol=3, loc="upper center", bbox_to_anchor=(0.5, -0.14))


def plot_operations(data):
    fig, ax = plt.subplots(figsize=(8, 6))
    for alg in ALGORITHMS:
        for d in DENSITIES:
            rows = data[(alg, d)]
            ax.plot([r["n"] for r in rows], [r["edge_scans"] for r in rows],
                    label=f"{alg} ({d})", **line_kwargs(alg, d))
    ax.set_xscale("log")
    ax.set_yscale("log")
    style(ax, "n (vertices)", "edgeScans (edges examined)", "Operations vs n (log-log); Prim = Kosaraju and Kahn = DAG paths overlap")
    fig.tight_layout()
    fig.savefig(PLOT_DIR / "operations_vs_n.png", dpi=150, bbox_inches="tight")
    plt.close(fig)


def plot_scans_per_edge(data):
    fig, ax = plt.subplots(figsize=(8, 6))
    for alg in ALGORITHMS:
        for d in DENSITIES:
            rows = data[(alg, d)]
            ax.plot([r["n"] for r in rows], [r["edge_scans"] / r["m"] for r in rows],
                    label=f"{alg} ({d})", **line_kwargs(alg, d))
    ax.set_xscale("log")
    ax.set_ylim(0, 2.3)
    style(ax, "n (vertices)", "edgeScans / m", "Scans per edge; Prim = Kosaraju and Kahn = DAG paths overlap")
    fig.tight_layout()
    fig.savefig(PLOT_DIR / "scans_per_edge.png", dpi=150, bbox_inches="tight")
    plt.close(fig)


def plot_kruskal_share(data):
    fig, ax = plt.subplots(figsize=(8, 6))
    for d in DENSITIES:
        rows = data[("Kruskal", d)]
        ax.plot([r["n"] for r in rows], [r["edge_scans"] / r["m"] for r in rows],
                STYLES[d], marker=MARKERS[d], label=f"Kruskal ({d}, m = {4 if d == 'sparse' else 20}n)")
    ax.set_xscale("log")
    ax.set_ylim(0, 1.05)
    style(ax, "n (vertices)", "edgeScans / m (share of edges examined)", "Share of edges Kruskal examines")
    fig.tight_layout()
    fig.savefig(PLOT_DIR / "kruskal_share.png", dpi=150, bbox_inches="tight")
    plt.close(fig)


def fmt(values, digits=3):
    return " | ".join(f"{v:.{digits}f}" for v in values)


def analysis(data):
    ns = [r["n"] for r in data[("Kruskal", "sparse")]]
    lines = []
    lines.append("## Slopes of edgeScans on log-log axes\n")
    lines.append("| Algorithm | sparse (m = 4n) | dense (m = 20n) |")
    lines.append("|---|---|---|")
    for alg in ALGORITHMS:
        s = [slope([r["n"] for r in data[(alg, d)]], [r["edge_scans"] for r in data[(alg, d)]]) for d in DENSITIES]
        lines.append(f"| {alg} | {s[0]:.3f} | {s[1]:.3f} |")
    lines.append("")
    lines.append("## Counter divided by formula\n")
    header = "| Algorithm | Ratio | Density | " + " | ".join(f"n={n}" for n in ns) + " |"
    sep = "|---|---|---|" + "---|" * len(ns)
    lines.append(header)
    lines.append(sep)

    def row(alg, label, d, fn):
        values = [fn(r) for r in data[(alg, d)]]
        lines.append(f"| {alg} | {label} | {d} | " + fmt(values) + " |")

    for d in DENSITIES:
        row("Kruskal", "dsuOps / edgeScans", d, lambda r: r["dsu_ops"] / r["edge_scans"])
    for d in DENSITIES:
        row("Kruskal", "dsuOps / E", d, lambda r: r["dsu_ops"] / r["m"])
    for d in DENSITIES:
        row("Kruskal", "comparisons / (E log2 E)", d, lambda r: r["comparisons"] / (r["m"] * math.log2(r["m"])))
    for d in DENSITIES:
        row("Prim", "comparisons / (E log2 V)", d, lambda r: r["comparisons"] / (r["m"] * math.log2(r["n"])))
    for d in DENSITIES:
        row("Prim", "edgeScans / (2E)", d, lambda r: r["edge_scans"] / (2 * r["m"]))
    for alg in ["Kosaraju", "Kahn", "DagShortestPath"]:
        for d in DENSITIES:
            row(alg, "edgeScans / (V + E)", d, lambda r: r["edge_scans"] / (r["n"] + r["m"]))
    lines.append("")
    lines.append("## Share of edges Kruskal examines (edgeScans / m)\n")
    lines.append("| Density | " + " | ".join(f"n={n}" for n in ns) + " |")
    lines.append("|---|" + "---|" * len(ns))
    for d in DENSITIES:
        values = [r["edge_scans"] / r["m"] for r in data[("Kruskal", d)]]
        lines.append(f"| {d} | " + fmt(values) + " |")
    lines.append("")
    lines.append("## Median wall-clock time, ms\n")
    lines.append("| Algorithm | Density | " + " | ".join(f"n={n}" for n in ns) + " |")
    lines.append("|---|---|" + "---|" * len(ns))
    for alg in ALGORITHMS:
        for d in DENSITIES:
            values = [r["time_ms"] for r in data[(alg, d)]]
            lines.append(f"| {alg} | {d} | " + fmt(values, 1) + " |")
    lines.append("")
    lines.append("## Maximum depth of the explicit stack (Kosaraju)\n")
    lines.append("| Density | " + " | ".join(f"n={n}" for n in ns) + " |")
    lines.append("|---|" + "---|" * len(ns))
    for d in DENSITIES:
        values = [str(r["max_depth"]) for r in data[("Kosaraju", d)]]
        lines.append(f"| {d} | " + " | ".join(values) + " |")
    lines.append("")
    return "\n".join(lines)


def main():
    PLOT_DIR.mkdir(parents=True, exist_ok=True)
    data = load()
    plot_operations(data)
    plot_scans_per_edge(data)
    plot_kruskal_share(data)
    text = analysis(data)
    ANALYSIS_PATH.write_text(text, encoding="utf-8")
    print(text)
    print(f"Wrote 3 plots to {PLOT_DIR} and analysis to {ANALYSIS_PATH}")


main()