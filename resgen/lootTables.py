from mcresources import loot_tables
from mcresources.type_definitions import Json


def boat_frame_flat(wood: str) -> list[Json]:
    return [{"name": "alekiships:watercraft_frame_flat"},
            [
                # Planks
                {"name": f"minecraft:{wood}_planks",
                 "conditions": [loot_tables.block_state_property(
                     f"alekiships:wood/watercraft_frame_flat/{wood}[frame_processed=0]")]},

                {"name": f"minecraft:{wood}_planks", "functions": loot_tables.set_count(2),
                 "conditions": [loot_tables.block_state_property(
                     f"alekiships:wood/watercraft_frame_flat/{wood}[frame_processed=1]")]},

                {"name": f"minecraft:{wood}_planks", "functions": loot_tables.set_count(3),
                 "conditions": [loot_tables.block_state_property(
                     f"alekiships:wood/watercraft_frame_flat/{wood}[frame_processed=2]")]},

                {"name": f"minecraft:{wood}_planks", "functions": loot_tables.set_count(4),
                 "conditions": [loot_tables.block_state_property(
                     f"alekiships:wood/watercraft_frame_flat/{wood}[frame_processed=3]")
                 ]}
            ]]


def boat_frame(wood: str) -> list[Json]:
    return [{"name": "alekiships:watercraft_frame_angled"},
            [
                # Planks
                {"name": f"minecraft:{wood}_planks",
                 "conditions": [loot_tables.block_state_property(
                     f"alekiships:wood/watercraft_frame_angled/{wood}[frame_processed=0]")]},

                {"name": f"minecraft:{wood}_planks", "functions": loot_tables.set_count(2),
                 "conditions": [loot_tables.block_state_property(
                     f"alekiships:wood/watercraft_frame_angled/{wood}[frame_processed=1]")]},

                {"name": f"minecraft:{wood}_planks", "functions": loot_tables.set_count(3),
                 "conditions": [loot_tables.block_state_property(
                     f"alekiships:wood/watercraft_frame_angled/{wood}[frame_processed=2]")]},

                {"name": f"minecraft:{wood}_planks", "functions": loot_tables.set_count(4),
                 "conditions": [loot_tables.block_state_property(
                     f"alekiships:wood/watercraft_frame_angled/{wood}[frame_processed=3]")]}
            ]]
