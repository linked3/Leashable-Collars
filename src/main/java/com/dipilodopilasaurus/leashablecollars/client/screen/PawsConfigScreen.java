package com.dipilodopilasaurus.leashablecollars.client.screen;

//? if >=1.19.3 {
import net.minecraft.core.registries.BuiltInRegistries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.BuiltInRegistries;
*///?}

import com.dipilodopilasaurus.leashablecollars.Text;

//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} elif >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
*///?}
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.ChatFormatting;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import com.dipilodopilasaurus.leashablecollars.network.Net;
import com.dipilodopilasaurus.leashablecollars.network.PacketPawsConfig;
import com.dipilodopilasaurus.leashablecollars.network.PacketSavePawsConfig;
import com.dipilodopilasaurus.leashablecollars.paws.PawsConfigHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/** The blocks and held-items allow-lists. Paged, not scrolled: the selection-list APIs move too often. */
public class PawsConfigScreen extends Screen {
    private static final int MAX_ROWS = 8;
    private static final int MAX_ROW_WIDTH = 300;
    /** Everything but the rows themselves: the title, the search box and the four button ranks. */
    private static final int CHROME_HEIGHT = 124;

    private record Row(String id, String name) {
        String haystack() {
            return (name + " " + id).toLowerCase(Locale.ROOT);
        }
    }

    private final UUID petId;
    private final int section;
    private final Screen parent;
    private final List<Row> all;
    private final Set<String> approved = new LinkedHashSet<>();
    private final List<Button> rowButtons = new ArrayList<>();

    private boolean unrestricted;
    private List<Row> filtered;
    private String search = "";
    private int page;
    private int top;
    private int rows = MAX_ROWS;
    private int listBottom;

    public PawsConfigScreen(PacketPawsConfig payload, String petName, Screen parent) {
        super(Text.translatable(payload.section() == PawsConfigHelper.ITEMS
                ? "gui.playercollars.paw_configurator.item.title"
                : "gui.playercollars.paw_configurator.block.title", petName));
        this.petId = payload.petId();
        this.section = payload.section();
        this.parent = parent;
        this.unrestricted = payload.unrestricted();
        this.approved.addAll(payload.entries());
        this.all = section == PawsConfigHelper.ITEMS ? items() : blocks();
        this.filtered = all;
    }

    private static List<Row> blocks() {
        List<Row> rows = new ArrayList<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            rows.add(new Row(BuiltInRegistries.BLOCK.getKey(block).toString(), block.getName().getString()));
        }
        rows.sort(Comparator.comparing(Row::id));
        return rows;
    }

    private static List<Row> items() {
        List<Row> rows = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            String id = BuiltInRegistries.ITEM.getKey(item).toString();
            if ("minecraft:air".equals(id)) continue;
            rows.add(new Row(id, Text.translatable(item.getDescriptionId()).getString()));
        }
        rows.sort(Comparator.comparing(Row::id));
        return rows;
    }

    // Sized to the window: at GUI scale 4 a 300x292 block does not fit, and a clipped row cannot be ticked.
    @Override
    protected void init() {
        int rowWidth = Math.min(MAX_ROW_WIDTH, this.width - 20);
        int half = rowWidth / 2 - 2;
        rows = Math.max(1, Math.min(MAX_ROWS, (this.height - 4 - CHROME_HEIGHT) / 21));
        top = Math.max(14, (this.height - (CHROME_HEIGHT + rows * 21)) / 2 + 14);
        listBottom = top + 26 + rows * 21;

        int x = this.width / 2 - rowWidth / 2;
        EditBox searchBox = GuiCompat.searchBox(this.font, x, top, rowWidth, 20,
                Text.translatable("gui.playercollars.paw_configurator.search"));
        searchBox.setMaxLength(100);
        searchBox.setResponder(this::onSearch);
        addRenderableWidget(searchBox);

        rowButtons.clear();
        for (int i = 0; i < rows; i++) {
            int row = i;
            rowButtons.add(addRenderableWidget(GuiCompat.button(Text.empty(), button -> toggle(row), x, top + 26 + i * 21, rowWidth, 20)));
        }

        addRenderableWidget(GuiCompat.button(Text.literal("<"), button -> turnPage(-1), x, listBottom + 16, 40, 20));
        addRenderableWidget(GuiCompat.button(Text.literal(">"), button -> turnPage(1), x + rowWidth - 40, listBottom + 16, 40, 20));
        addRenderableWidget(GuiCompat.button(Text.translatable("gui.playercollars.paw_configurator.enable_all"), button -> setAll(true), x, listBottom + 40, half, 20));
        addRenderableWidget(GuiCompat.button(Text.translatable("gui.playercollars.paw_configurator.disable_all"), button -> setAll(false), x + rowWidth - half, listBottom + 40, half, 20));
        addRenderableWidget(GuiCompat.button(Text.translatable("gui.done"), button -> save(), x, listBottom + 64, half, 20));
        addRenderableWidget(GuiCompat.button(Text.translatable("gui.cancel"), button -> onClose(), x + rowWidth - half, listBottom + 64, half, 20));

        refresh();
    }

    private void onSearch(String text) {
        search = text == null ? "" : text.toLowerCase(Locale.ROOT).trim();
        page = 0;
        if (search.isEmpty()) {
            filtered = all;
        } else {
            List<Row> matches = new ArrayList<>();
            for (Row row : all) {
                if (row.haystack().contains(search)) matches.add(row);
            }
            filtered = matches;
        }
        refresh();
    }

    private void turnPage(int delta) {
        page = Math.max(0, Math.min(pageCount() - 1, page + delta));
        refresh();
    }

    private void setAll(boolean enabled) {
        unrestricted = enabled;
        approved.clear();
        refresh();
    }

    /** Leaving "everything" has to materialise the list first, or the untick would have nothing to remove. */
    private void toggle(int rowIndex) {
        int index = page * rows + rowIndex;
        if (index >= filtered.size()) return;

        if (unrestricted) {
            unrestricted = false;
            for (Row row : all) approved.add(row.id());
        }
        String id = filtered.get(index).id();
        if (!approved.remove(id)) approved.add(id);
        refresh();
    }

    private void refresh() {
        page = Math.max(0, Math.min(pageCount() - 1, page));
        for (int i = 0; i < rows; i++) {
            Button button = rowButtons.get(i);
            int index = page * rows + i;
            if (index >= filtered.size()) {
                button.visible = false;
                continue;
            }
            Row row = filtered.get(index);
            boolean allowed = unrestricted || approved.contains(row.id());
            button.visible = true;
            button.setMessage(Text.literal(allowed ? "✓ " : "✕ ")
                    .withStyle(allowed ? ChatFormatting.GREEN : ChatFormatting.RED)
                    .append(Text.literal(row.name() + " ").withStyle(ChatFormatting.WHITE))
                    .append(Text.literal(row.id()).withStyle(ChatFormatting.DARK_GRAY)));
        }
    }

    private int pageCount() {
        return Math.max(1, (filtered.size() + rows - 1) / rows);
    }

    private void save() {
        // Ticking every row by hand means the same thing as Enable All, and stores far less on the stack.
        boolean everything = unrestricted || approved.size() == all.size();
        Net.sendToServer(new PacketSavePawsConfig(petId, section, everything,
                everything ? List.of() : List.copyOf(approved), true, true));
        onClose();
    }

    @Override
    public void onClose() {
        GuiCompat.setScreen(minecraft, parent);
    }

    // Signature guarded, body shared -- see GuiCompat.
    //? if >=26.1 {
    /*@Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
    *///?} elif >=1.20 {
    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    //?} else {
    /*@Override
    public void render(PoseStack context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    *///?}
        GuiCompat.centeredText(context, font, title, this.width / 2, top - 14, -1);
        GuiCompat.centeredText(context, font, Text.translatable("gui.playercollars.paw_configurator.count",
                filtered.size(), all.size()), this.width / 2, listBottom + 3, -2236963);
        GuiCompat.centeredText(context, font, Text.literal((page + 1) + " / " + pageCount()),
                this.width / 2, listBottom + 22, -1);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
