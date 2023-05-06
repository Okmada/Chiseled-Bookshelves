package me.adam.theater;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChiseledBookshelfBlock;

public class Set {

    public final BlockState[] blockStateArray = new BlockState[64];

    public Set() {
        for (int i = 0; i < 64; ++i) {
            String binary = Integer.toBinaryString(i);
            String[] s = ("0".repeat(6 - binary.length()) + binary).split("");

            BlockState state = Blocks.CHISELED_BOOKSHELF.getDefaultState();

            for (int u = 0; u < 6; ++u) {
                if (!s[u].equals("1")) continue;
                state = state.with(ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES.get(u), true);
            }

            blockStateArray[i] = state;
        }
    }
}
