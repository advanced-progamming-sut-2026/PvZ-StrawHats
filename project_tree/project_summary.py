from pathlib import Path
import re

OUTPUT_DIR = Path(__file__).parent
ROOT = OUTPUT_DIR.parent

JAVA_FILES = sorted(ROOT.rglob("*.java"))

def tree(path, prefix=""):
    entries = sorted(path.iterdir(), key=lambda p: (p.is_file(), p.name.lower()))
    for i, entry in enumerate(entries):
        connector = "└── " if i == len(entries) - 1 else "├── "
        lines.append(prefix + connector + entry.name)
        if entry.is_dir():
            extension = "    " if i == len(entries) - 1 else "│   "
            tree(entry, prefix + extension)

lines = [ROOT.resolve().name]
tree(ROOT)

with open(OUTPUT_DIR / "project_tree.txt", "w", encoding="utf8") as f:
    f.write("\n".join(lines))

class_pattern = re.compile(r'class\s+(\w+)')
extends_pattern = re.compile(r'extends\s+(\w+)')
implements_pattern = re.compile(r'implements\s+([^{]+)')
package_pattern = re.compile(r'package\s+([\w\.]+)')
import_pattern = re.compile(r'import\s+([\w\.]+)')
method_pattern = re.compile(r'(public|private|protected)\s+[^\(\n]+\s+(\w+)\s*\(')

summary = []
dependencies = []

for file in JAVA_FILES:
    text = file.read_text(errors="ignore")

    summary.append("# " + str(file))
    summary.append("")

    pkg = package_pattern.search(text)
    if pkg:
        summary.append("Package: " + pkg.group(1))

    cls = class_pattern.search(text)
    if cls:
        classname = cls.group(1)
        summary.append("Class: " + classname)
    else:
        continue

    ext = extends_pattern.search(text)
    if ext:
        summary.append("Extends: " + ext.group(1))
        dependencies.append((classname, ext.group(1), "extends"))

    imp = implements_pattern.search(text)
    if imp:
        summary.append("Implements: " + imp.group(1).replace("\n", ""))

    imports = import_pattern.findall(text)

    summary.append("")
    summary.append("Imports:")

    for i in imports:
        summary.append("  - " + i)
        dep = i.split(".")[-1]
        dependencies.append((classname, dep, "uses"))

    methods = method_pattern.findall(text)

    summary.append("")
    summary.append("Methods:")

    for _, m in methods:
        summary.append("  - " + m + "()")

    summary.append("")
    summary.append("-" * 60)
    summary.append("")

with open(OUTPUT_DIR / "class_summary.md", "w", encoding="utf8") as f:
    f.write("\n".join(summary))

dep = []

for a, b, t in sorted(set(dependencies)):
    dep.append(f"{a} --{t}--> {b}")

with open(OUTPUT_DIR / "dependency_graph.md", "w", encoding="utf8") as f:
    f.write("\n".join(dep))

print()
print("Done!")
print(f"Output directory: {OUTPUT_DIR.resolve()}")
print("Generated:")
print(" - project_tree.txt")
print(" - class_summary.md")
print(" - dependency_graph.md")
