# FeatherJet Examples

This directory contains example applications demonstrating different ways to use FeatherJet.

## Examples Available

### 1. Simple Todo API (`todo-api/`)
A basic REST API for managing todos, demonstrating:
- CRUD operations
- JSON responses
- Error handling
- Route parameters

### 2. Static Blog (`static-blog/`)
A simple static blog website showing:
- Static file serving
- CSS and JavaScript
- Image handling
- Multiple pages

### 3. API + Frontend (`full-stack/`)
A complete example combining:
- Backend REST API
- Frontend application
- API integration
- Real-time updates

## Running Examples

1. Copy the example directory to your FeatherJet root
2. Update the `config.yaml` to point to the example's public directory
3. Add any custom handlers to your main server code
4. Run FeatherJet

```bash
# Example: Running the static blog
cp -r examples/static-blog/public ./public-blog
# Update config.yaml to use ./public-blog as static directory
./featherjet
```

## Creating Your Own Examples

Feel free to contribute new examples! Each example should include:
- A README.md explaining the example
- All necessary files (HTML, CSS, JS, etc.)
- Clear instructions for setup and usage
