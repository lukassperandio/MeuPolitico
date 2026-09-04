# MeuPolítico — Design

## Mode
**Operate** — the visitor came to look up politicians and read official data.
Scanability and trust beat decoration.

## Platform
Responsive web (Angular). Mobile-first is important; desktop is the main work surface for tables and filters.

## Visual direction
Institutional transparency product for Brazil.
Calm, clear, credible — closer to a civic tool than a startup landing page.

### Color
- Primary: deep blue `#0B3D62` (headers, primary actions, brand)
- Secondary / accent: green `#0F9B6C` (trust, “official”, highlights)
- Pale blue `#E8F0F7` and soft green `#E3F6ED` for chips and soft surfaces
- Background: warm gray-green `#F3F6F5`
- Surface / card: `#FFFFFF`
- Border: `#E2E8E5`
- Text: ink `#10201B`, muted `#5C6B67`
- Danger / high spend signal: `#D64545` (sparingly)
- Do **not** use purple-to-blue gradients, neon, or glassmorphism

### Typography
- Display / titles: Space Grotesk (or equivalent geometric sans), semibold/bold
- Body / UI: Inter (or system-ui stack)
- Numbers, money, codes, labels: IBM Plex Mono (tabular where possible)
- Clear hierarchy: one strong page title, section titles secondary, body readable at 15–16px
- Avoid “Inter for everything” with no hierarchy

### Layout & components
- Max content width ~960px centered
- Cards: single border, light shadow, radius ~16px — no nested card-in-card stacks
- Lists: politician rows with photo, name, party chip, state, source chip
- Profile: optional one gradient hero (deep blue → green) as the only strong brand moment; rest of page quiet
- Tables: dense but readable; mono for amounts
- Notes about data sources: short, visible, not alarmist — pale background, not thick rainbow side-bars everywhere
- Empty states: plain sentence + what data is missing (e.g. assets only in election years)

### Motion
- Minimal. Prefer 150–200ms opacity/transform on hover if any
- No bouncing badges, gradient text animation, or decorative floating orbs
- Respect prefers-reduced-motion

### Interaction
- Filters update with debounce on text; immediate on select/sort
- Primary button: solid deep blue
- Secondary: white + border
- Focus states visible (green-tinted outline is fine)

## Explicit anti-patterns (AI slop)
- Purple/violet gradients on buttons or backgrounds
- Glassmorphism, blur orbs, neon glow
- Side-colored “tab” stripe on every card
- Cards nested three levels deep
- Gray text on colored backgrounds with poor contrast
- Rounded-square icon tile above every heading as decoration
- Motion without purpose

## Product-specific UI rules
- Always make the **official source** obvious (Câmara, TSE)
- Asset section must note: updates on **election years** (e.g. 2022, 2026), not yearly in office
- Attendance: presence counts from official event lists; do not imply full absence accounting if data does not support it
- Rankings: top 50 is enough; empty rankings need a clear empty state

## Voice in UI copy
Portuguese (Brazil). Direct, neutral, civic.
No hype (“revolucionário”, “mágico”). Prefer “dados oficiais”, “fonte”, “sem dados ainda”.