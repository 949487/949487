import json
from pathlib import Path

ROW_COUNT = 24
COLUMN_COUNT = 20

placements = [
    ("quiet-night-thought", "床前明月光", "H", 6, 2),
    ("quiet-night-thought", "举头望明月", "V", 3, 4),
    ("mutual-longing", "红豆生南国", "H", 10, 10),
    ("mutual-longing", "春来发几枝", "V", 13, 15),
    ("on-the-stork-tower", "白日依山尽", "H", 16, 1),
    ("on-the-stork-tower", "黄河入海流", "V", 12, 18),
    ("waterfall", "飞流直下三千尺", "H", 20, 2),
    ("waterfall", "疑是银河落九天", "V", 4, 0),
]

poems = json.loads(Path("app/src/main/assets/poems.json").read_text(encoding="utf-8"))
poem_ids = {poem["id"] for poem in poems}
assert len(poems) == 4
assert all(poem_id in poem_ids for poem_id, *_ in placements)

board = {}
for poem_id, line_text, orientation, row, column in placements:
    for index, ch in enumerate(line_text):
        r = row if orientation == "H" else row + index
        c = column + index if orientation == "H" else column
        assert 0 <= r < ROW_COUNT and 0 <= c < COLUMN_COUNT, (poem_id, line_text, r, c)
        board.setdefault((r, c), set()).add(ch)

assert any(len(chars) == 1 for chars in board.values())
assert all(len(chars) == 1 for chars in board.values()), board
assert len(board) < ROW_COUNT * COLUMN_COUNT
print("layout ok")
