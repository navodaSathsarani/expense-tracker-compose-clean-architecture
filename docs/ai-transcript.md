# AI usage transcript (summary)

This file satisfies the assessment requirement for **evidence of AI-assisted development**. No API keys, tokens, or credentials are included.

## Tools

| Tool | Role |
|------|------|
| **Cursor (Claude)** | Primary IDE agent: scaffolding, refactors, Gradle fixes, README/ADR drafts |
| **Human (Navoda)** | Architecture decisions, code review, commit messages, final ADR wording |

## Session scope (high level)

Work was done in phased Cursor sessions on this repository, including:

1. **Architecture & features** — Clean Architecture layers, Room SSOT, Hilt, Compose screens (list, add, filter, summary), navigation, mock `ExpenseApi` with filter/summary contract.
2. **Domain tests** — Use case unit tests (Add, Delete, Filter, GetSummary, Refresh) with MockK.
3. **Documentation** — Root `README.md` (architecture diagrams, build steps, assumptions, KMP “more time”), `ADR.md` (001–005).
4. **Build fixes** — JVM 17 toolchain alignment; Hilt 2.56.2 for Kotlin 2.1 kapt metadata; Material3 category picker (`DropdownMenu` instead of version-fragile `ExposedDropdownMenu`).
5. **Submission polish** — Delete confirmation `AlertDialog`, CI workflow, `strings.xml` externalization, submission checklist in README.

## Representative prompts (paraphrased)

- Implement lead-band plan: filter dates, `RefreshExpensesUseCase`, repository summary, strings, CI, full README at repo root.
- Fix JVM target mismatch (`kspDebugKotlin` 21 vs Java 11) and Hilt “unsupported Kotlin metadata version”.
- Restore full README for assessors; document KMP as “with more time” and assumptions.
- Add delete confirmation before calling `DeleteExpenseUseCase`.
- Submission gap review: AI transcript file, README drift, verify build.

## Human decisions retained

- **Android-only** for the time-boxed assessment (KMP deferred — ADR-004).
- **Filter logic** — when both category and date range are set, match **OR**; otherwise **AND** (`FilterExpensesUseCase`).
- **Offline-first** — Room as SSOT; mock sync fire-and-forget.
- **Testing scope** — domain use cases only; no Compose UI tests in scope.

## Full chat export (optional)

If evaluators need the raw thread, export from Cursor (**Share** or export chat) and add the link below:

```
<!-- Replace with your shared Cursor link before external submission -->
Cursor session export: (add link here)
```

Internal reference (developer machine): agent transcript id `8582464e-8ce5-4c69-927e-96fc248ed3fc` in Cursor project history for `expense-tracker-compose-clean-architecture`.

## Security note

Before sharing externally, confirm this file and any linked export contain **no** `.env`, signing keys, or personal tokens.
