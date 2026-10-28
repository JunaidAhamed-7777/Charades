"""Generate the app's plain-text legal documents from the maintained Markdown."""

from __future__ import annotations

import argparse
from pathlib import Path
import re
import sys


ROOT = Path(__file__).resolve().parents[1]
POLICIES = (
    ("docs/PRIVACY_POLICY.md", "app/src/main/assets/privacy_policy.txt"),
    ("docs/TERMS_OF_SERVICE.md", "app/src/main/assets/terms_of_service.txt"),
    ("LICENSE", "app/src/main/assets/license.txt"),
)


def plain_text(markdown: str) -> str:
    """Current policies use only headings and plain paragraphs."""
    return re.sub(r"(?m)^#{1,6} ", "", markdown).rstrip() + "\n"


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true", help="Fail on stale app text.")
    args = parser.parse_args()
    mismatches: list[str] = []
    for source_name, destination_name in POLICIES:
        expected = plain_text((ROOT / source_name).read_text(encoding="utf-8"))
        destination = ROOT / destination_name
        actual = destination.read_text(encoding="utf-8") if destination.exists() else None
        if actual == expected:
            continue
        if args.check:
            mismatches.append(destination_name)
        else:
            destination.parent.mkdir(parents=True, exist_ok=True)
            destination.write_text(expected, encoding="utf-8", newline="\n")
            print(f"Updated {destination_name}")
    if mismatches:
        print("Out-of-date legal assets: " + ", ".join(mismatches), file=sys.stderr)
        print("Run: python scripts/sync_policies.py", file=sys.stderr)
        return 1
    if args.check:
        print("Legal documents match the bundled app text.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
