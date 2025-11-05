# Phase 3: Intelligent Non-Destructive Synchronization

## Status: 🏗️ Architecture Defined - Implementation Pending

Phase 3 architecture has been fully designed and all interfaces are defined. This document explains what Phase 3 will do and how to contribute to its implementation.

## What Problem Does Phase 3 Solve?

### The "Eject Button" Problem

Current state (Phase 2):
```
1. Designer creates UI in Figma → 
2. Developer generates code → 
3. Developer adds backend logic (onClick, API calls, Django tags) → 
4. Designer updates color in Figma → 
5. ❌ Developer can't re-sync without losing all their work
```

Phase 3 solution:
```
1. Designer creates UI in Figma →
2. Developer generates code →
3. Developer adds backend logic →
4. Designer updates color in Figma →
5. ✅ Developer clicks "Sync" - only colors update, logic preserved!
```

## The Four Pillars of Phase 3

### 1. Figma Annotation Parser
**What**: Understands special naming in Figma
**Example**: Node named `text:{{ user.name }}` → Plugin knows it needs backend data

### 2. Backend Introspection
**What**: Reads your Django/Flask/FastAPI code to find available data
**Example**: Finds `User.username` in models.py → Available for binding

### 3. Data Binding Manager
**What**: Maps Figma elements to backend data
**Example**: `{{ user.name }}` in Figma → `context['current_user'].username` in Django

### 4. Non-Destructive Sync
**What**: Updates design without destroying your code
**Example**: Preserves your `{% if user.is_staff %}` while updating CSS

## Architecture Files

All Phase 3 interfaces and models are defined:

### Models (`src/main/java/com/robwestz/figmatobackend/models/`)
- ✅ `DataBinding.java` - Maps Figma → Backend
- ✅ `BackendDataSource.java` - Available backend data
- ✅ `SyncContext.java` - Complete sync state

### Interfaces (`src/main/java/com/robwestz/figmatobackend/`)

#### Binding Package
- ✅ `FigmaAnnotationParser.java` - Parse Figma naming conventions
- ✅ `DataBindingManager.java` - Manage binding mappings

#### Introspection Package
- ✅ `BackendIntrospectionService.java` - Main introspection interface
- ✅ `DjangoIntrospector.java` - Django-specific discovery
- ✅ `FlaskIntrospector.java` - Flask-specific discovery
- ✅ `FastAPIIntrospector.java` - FastAPI-specific discovery

#### Sync Package
- ✅ `NonDestructiveSyncService.java` - Smart code merging
- ✅ `Phase3SyncCoordinator.java` - Orchestrates everything

### Documentation
- ✅ `PHASE3_ARCHITECTURE.md` - Complete technical architecture
- ✅ `PHASE3_README.md` - This file

## Implementation Roadmap

### Milestone 1: Annotation Parser (2 weeks)
**Goal**: Parse Figma node names to identify bindings

Tasks:
- [ ] Implement regex patterns for each binding type
- [ ] Create `FigmaAnnotationParserImpl.java`
- [ ] Write unit tests for pattern matching
- [ ] Handle edge cases (malformed annotations)

**Success Criteria**: Given Figma file, extract all binding points

### Milestone 2: Django Introspection (2 weeks)
**Goal**: Discover Django models and view context

Tasks:
- [ ] Use PyCharm PSI to find models.py files
- [ ] Parse Model classes and extract fields
- [ ] Find render() calls in views.py
- [ ] Extract context dictionaries
- [ ] Create `DjangoIntrospectorImpl.java`
- [ ] Write integration tests with sample Django project

**Success Criteria**: Given Django project, list all available data sources

### Milestone 3: Binding Manager (2 weeks)
**Goal**: Store and manage bindings

Tasks:
- [ ] Implement CRUD operations for bindings
- [ ] Create persistence layer (XML in .idea/)
- [ ] Implement fuzzy matching algorithm
- [ ] Add validation logic
- [ ] Create `DataBindingManagerImpl.java`
- [ ] Write tests for matching and persistence

**Success Criteria**: Create, save, load, and validate bindings

### Milestone 4: Non-Destructive Sync (2 weeks)
**Goal**: Merge code without destroying manual changes

Tasks:
- [ ] Implement code marker system
- [ ] Create checksum calculation
- [ ] Build merge algorithm
- [ ] Handle conflicts
- [ ] Create `NonDestructiveSyncServiceImpl.java`
- [ ] Extensive testing with real code files

**Success Criteria**: Update design, preserve manual code

### Milestone 5: UI Integration (2 weeks)
**Goal**: Add Data Binding tab to tool window

Tasks:
- [ ] Design UI layout
- [ ] Implement two-column binding view
- [ ] Add drag-and-drop or click-to-map
- [ ] Show sync preview
- [ ] Add to `FigmaToolWindowPanel`

**Success Criteria**: User can visually map bindings

### Milestone 6: Flask & FastAPI (2 weeks)
**Goal**: Support additional frameworks

Tasks:
- [ ] Implement `FlaskIntrospectorImpl.java`
- [ ] Implement `FastAPIIntrospectorImpl.java`
- [ ] Test with sample projects
- [ ] Update documentation

**Success Criteria**: Works with Django, Flask, and FastAPI

## How to Contribute

### For Implementers

1. **Pick a Milestone**: Choose from roadmap above
2. **Create Implementation**: 
   - Name class with `Impl` suffix (e.g., `FigmaAnnotationParserImpl`)
   - Implement the corresponding interface
   - Follow existing code style
3. **Write Tests**: Add to `src/test/java/`
4. **Update plugin.xml**: Uncomment service registration
5. **Submit PR**: Reference this document

### For Testers

1. **Test with Real Projects**: Try with actual Django/Flask/FastAPI projects
2. **Report Edge Cases**: Find patterns we haven't considered
3. **Validate Bindings**: Check type compatibility
4. **Test Merging**: Verify manual code is preserved

### For Designers

1. **Document Naming Conventions**: Help refine annotation syntax
2. **Create Test Designs**: Build Figma files for testing
3. **UI/UX Feedback**: Suggest improvements to binding interface

## Example Usage (When Complete)

### Step 1: Designer Annotates in Figma
```
Create frame "User Card"
├─ Text: "text:{{ user.name }}"
├─ Text: "text:{{ user.email }}"
└─ Button: "button:[action:edit_user]"
```

### Step 2: Developer Opens PyCharm
```
Open Figma tool window → Data Binding tab
Shows:
  Figma Side:                Backend Side:
  {{ user.name }}    →       current_user.username
  {{ user.email }}   →       current_user.email
  [action:edit_user] →       edit_user_view()
```

### Step 3: Map and Sync
```
Click "Sync"
Result:
  ✅ Generated: user_card.html with Django template tags
  ✅ Preserved: Your custom {% if %} blocks
  ✅ Applied: 3 data bindings
```

### Step 4: Designer Updates Figma
```
Changes button color from blue to green
```

### Step 5: Developer Re-Syncs
```
Click "Sync" again
Result:
  ✅ Updated: Button CSS color
  ✅ Preserved: Your onClick handler
  ✅ Preserved: Your validation logic
  ✅ Kept: All 3 data bindings
```

## Technical Deep Dive

See `PHASE3_ARCHITECTURE.md` for:
- Detailed component diagrams
- PSI API usage examples
- Merge algorithm pseudocode
- Testing strategies
- Edge case handling

## Questions?

- **Architecture questions**: See `PHASE3_ARCHITECTURE.md`
- **Implementation questions**: Check interface JavaDocs
- **General questions**: Open a GitHub issue

## Current Status Summary

- ✅ All interfaces defined
- ✅ All models created
- ✅ Architecture documented
- ✅ Roadmap planned
- ⏳ Implementation pending
- ⏳ UI updates pending
- ⏳ Testing pending

**Next Steps**: Begin Milestone 1 (Annotation Parser) implementation.

---

**Phase 3 transforms "one-time code generator" into "intelligent design-code sync partner"**
