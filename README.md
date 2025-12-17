# API Blueprint (Lifetime) — IntelliJ Platform plugin

This plugin adds support for the [API Blueprint](https://apiblueprint.org/) format (`.apib`) inside IntelliJ-based IDEs.

## Main features

- **`.apib` file type support**
  - Recognizes files with the `apib` extension and treats them as Markdown-based API Blueprint documents.

- **API Blueprint semantic syntax highlighting**
  - Highlights common API Blueprint structures (sections, keywords, HTTP methods, parameters, etc.) on top of the Markdown language.

- **Customizable colors**
  - Exposes API Blueprint-specific highlighting keys in the IDE color settings so you can adapt the theme to your preference.

- **Startup checks & notifications**
  - Runs a lightweight startup activity and uses IDE notifications for important messages.
  - Includes license validation for the “Lifetime” distribution.

## Getting started

1. Install the plugin from **Settings/Preferences → Plugins**.
2. Open an existing `.apib` file (or create a new one).

You can use the sample files in this repository:

- `example-api.apib`
- `test-comprehensive.apib`

## Configure highlighting

Go to **Settings/Preferences → Editor → Color Scheme** and look for the API Blueprint-related entries (provided by this plugin) to adjust colors and styles.

## Compatibility

The plugin targets IntelliJ Platform IDEs and depends on the bundled Markdown support (`org.intellij.plugins.markdown`).

## License

See [`LICENSE`](./LICENSE).
