# Contributing to FeatherJet

Thank you for your interest in contributing to FeatherJet! This document provides guidelines and information for contributors.

## Code of Conduct

By participating in this project, you agree to abide by our code of conduct. Be respectful, inclusive, and professional in all interactions.

## How to Contribute

### Reporting Bugs

1. Check the [Issues](https://github.com/Rishabh-Tamrakar/FeatherJet/issues) page to see if the bug has already been reported
2. If not, create a new issue with:
   - Clear description of the bug
   - Steps to reproduce
   - Expected vs actual behavior
   - Environment details (Java version, OS, etc.)
   - Log files if applicable

### Suggesting Features

1. Check existing [Issues](https://github.com/Rishabh-Tamrakar/FeatherJet/issues) for similar suggestions
2. Create a new issue with:
   - Clear description of the feature
   - Use case and motivation
   - Proposed implementation approach (if any)

### Pull Requests

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes following our coding standards
4. Add tests for new functionality
5. Ensure all tests pass: `mvn test`
6. Update documentation if needed
7. Commit with clear, descriptive messages
8. Push to your fork: `git push origin feature/amazing-feature`
9. Create a Pull Request

## Development Setup

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git

### Setup

```bash
# Clone the repository
git clone https://github.com/Rishabh-Tamrakar/FeatherJet.git
cd FeatherJet

# Build the project
mvn clean compile

# Run tests
mvn test

# Create distribution
mvn clean package
```

### Running from Source

```bash
# Compile and run
mvn compile exec:java -Dexec.mainClass="com.featherjet.server.FeatherJetServer"

# Or build JAR and run
mvn clean package
java -jar target/featherjet-server-1.0.0.jar
```

## Coding Standards

### Java Style

- Use 4 spaces for indentation
- Follow standard Java naming conventions
- Add JavaDoc comments for public methods and classes
- Keep methods small and focused
- Use meaningful variable and method names

### Code Organization

- One public class per file
- Organize imports (remove unused)
- Group related functionality in packages
- Keep dependencies minimal

### Testing

- Write unit tests for new functionality
- Aim for good test coverage
- Use descriptive test method names
- Test both success and failure scenarios

### Documentation

- Update README.md for user-facing changes
- Add JavaDoc comments for public APIs
- Include code examples where helpful
- Update CHANGELOG.md for all changes

## Performance Considerations

- FeatherJet aims to be lightweight (~10MB)
- Avoid adding heavy dependencies
- Profile performance for critical paths
- Consider memory usage in design decisions

## Security Guidelines

- Validate all inputs
- Avoid path traversal vulnerabilities
- Handle errors gracefully without exposing internals
- Follow secure coding practices

## Release Process

1. Update version in `pom.xml`
2. Update `CHANGELOG.md`
3. Create release tag: `git tag v1.0.1`
4. Build distribution: `mvn clean package`
5. Create GitHub release with artifacts

## Questions?

- Open an [Issue](https://github.com/Rishabh-Tamrakar/FeatherJet/issues) for questions
- Start a [Discussion](https://github.com/Rishabh-Tamrakar/FeatherJet/discussions) for general topics
- Contact maintainers for urgent matters

Thank you for contributing to FeatherJet! 🚀
