# Phase 3 Architecture: Intelligent Non-Destructive Synchronization

## Overview

Phase 3 transforms the plugin from a one-time code generator into an intelligent synchronization partner that preserves manual code modifications while keeping design and code in sync.

## The Problem We're Solving

**The "Eject Button" Problem**: Developers generate code from Figma, add backend logic (onClick handlers, API calls, Django template tags), then can never re-sync because updates would destroy their work.

**Our Solution**: Intelligent AST-based code surgery that:
- Updates only visual/structural changes from Figma
- Preserves all manual code modifications
- Maintains data bindings between Figma and backend

## Architecture Components

### 1. Models (Data Structures)

#### DataBinding
Maps Figma elements to backend data sources.
```java
DataBinding {
    figmaNodeId: String          // e.g., "123:456"
    figmaExpression: String      // e.g., "{{ user.name }}"
    backendPath: String          // e.g., "context['current_user'].username"
    bindingType: ENUM            // TEXT_VARIABLE, ACTION, LOOP, CONDITIONAL
}
```

#### BackendDataSource
Represents discovered backend data.
```java
BackendDataSource {
    name: String                 // e.g., "user.name"
    type: ENUM                   // VARIABLE, LIST, ACTION, MODEL_FIELD
    dataType: String             // e.g., "str", "List[Product]"
    filePath: String             // Where it was found
}
```

#### SyncContext
Complete state for a sync operation.
```java
SyncContext {
    figmaFile: FigmaFile
    bindings: List<DataBinding>
    availableDataSources: List<BackendDataSource>
    existingFileChecksums: Map<String, String>
    frameworkType: ENUM          // DJANGO, FLASK, FASTAPI
}
```

### 2. Task 1: Figma Annotation Parser

**Interface**: `FigmaAnnotationParser`

**Purpose**: Parse Figma node names to identify data binding intentions.

**Supported Patterns**:
- `text:{{ user.name }}` → TEXT_VARIABLE binding
- `button:[action:submit_form]` → ACTION binding
- `list:[for:product in products]` → LOOP binding
- `[if:is_logged_in]` → CONDITIONAL binding

**Example**:
```java
// Designer names node in Figma: "text:{{ user.email }}"
DataBinding binding = parser.parseNodeName("text:{{ user.email }}", "123:456");
// Result: binding.type = TEXT_VARIABLE, binding.expression = "{{ user.email }}"
```

**Implementation Steps**:
1. Traverse Figma document tree
2. Check each node name against patterns
3. Extract binding information
4. Create DataBinding objects
5. Return list of all bindings found

### 3. Task 2: Backend Introspection

**Interface**: `BackendIntrospectionService`

**Purpose**: Use PyCharm PSI to discover available data in backend code.

**Framework-Specific Implementations**:

#### Django (`DjangoIntrospector`)
```python
# What we're looking for in models.py:
class User(models.Model):
    username = models.CharField(max_length=100)  # → BackendDataSource
    email = models.EmailField()                   # → BackendDataSource

# What we're looking for in views.py:
def profile(request):
    return render(request, 'profile.html', {
        'current_user': request.user,  # → BackendDataSource
        'posts': Post.objects.all()    # → BackendDataSource (LIST)
    })
```

**Discovery Process**:
1. Use PSI to find Python files matching patterns
2. Parse AST to identify:
   - Model classes and fields
   - render() calls and context dicts
   - Form fields
3. Extract type information
4. Build BackendDataSource objects

#### Flask (`FlaskIntrospector`)
```python
# What we're looking for:
@app.route('/users')
def users():
    users = User.query.all()
    return render_template('users.html', users=users)  # → BackendDataSource (LIST)
```

#### FastAPI (`FastAPIIntrospector`)
```python
# What we're looking for:
@app.get('/api/users', response_model=List[User])
def get_users():
    return users  # → BackendDataSource from Pydantic model
```

### 4. Task 3: Data Binding Manager

**Interface**: `DataBindingManager`

**Purpose**: Manage mappings and persistence.

**Functionality**:
- Create/update/delete bindings
- Suggest matches (fuzzy matching between Figma and backend)
- Validate bindings
- Persist to project settings (`.idea/figma-bindings.xml`)

**Matching Algorithm**:
```java
// Figma: "{{ user_email }}"
// Backend options: ["user.email", "current_user.email", "form.email"]
// Suggestions (ranked):
// 1. user.email (name match: 80%, type: str)
// 2. current_user.email (name match: 60%, type: str)
// 3. form.email (name match: 40%, type: str)
```

### 5. Task 4: Non-Destructive Sync

**Interface**: `NonDestructiveSyncService`

**Purpose**: Perform surgical code updates preserving manual changes.

**The Magic: Code Markers**

Generated code is wrapped:
```html
<!-- FIGMA_GEN:START:abc123:user-profile -->
<div class="user-profile">
    <span class="username">{{ user.name }}</span>
</div>
<!-- FIGMA_GEN:END:user-profile -->
```

Manual code is preserved:
```html
<!-- Developer adds this: -->
{% if user.is_authenticated %}
    <!-- FIGMA_GEN:START:abc123:user-profile -->
    <div class="user-profile">
        <span class="username">{{ user.name }}</span>
    </div>
    <!-- FIGMA_GEN:END:user-profile -->
{% endif %}
```

**Sync Algorithm**:
```
1. Calculate new checksum for Figma design
2. Read existing file
3. Extract regions:
   - FIGMA_GEN blocks (generated code)
   - Manual code (everything else)
4. Compare checksums:
   - If same: no changes needed
   - If different: design was updated
5. Generate new code with current design
6. Apply data bindings
7. Merge:
   - Replace old FIGMA_GEN blocks with new
   - Keep all manual code untouched
8. Write back to file
```

**Binding Application**:
```html
<!-- Before binding application: -->
<span class="username">John Doe</span>

<!-- After binding application (Django): -->
<span class="username">{{ current_user.username }}</span>

<!-- After binding application (React): -->
<span className="username">{user.name}</span>
```

### 6. Phase 3 Coordinator

**Interface**: `Phase3SyncCoordinator`

**Purpose**: Orchestrate the entire pipeline.

**Full Sync Workflow**:
```
User clicks "Sync" button
    ↓
1. Fetch Figma file via API
    ↓
2. Parse Figma annotations
    → Extract binding points: [{{ user.name }}, [action:submit]]
    ↓
3. Introspect backend
    → Discover available data: [user.name, submit_form()]
    ↓
4. Load existing bindings from settings
    → user.name → context['current_user'].username
    ↓
5. Generate code with bindings applied
    → <span>{{ current_user.username }}</span>
    ↓
6. Merge with existing files
    → Preserve manual {% if %} blocks
    → Update only FIGMA_GEN sections
    ↓
7. Write files
    ↓
8. Show sync result
    → "Updated 3 files, preserved 12 manual code blocks"
```

## User Interface Changes

### New Tool Window Tab: "Data Binding"

```
┌─────────────────────────────────────────────────────────────┐
│ Figma Tool Window                                  [×]       │
├─────────────────────────────────────────────────────────────┤
│ [Generate] [Sync] [Data Binding]                            │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  Figma Bindings          →         Backend Data             │
│  ┌─────────────────┐              ┌──────────────────┐      │
│  │ {{ user.name }} │──────────────│ user.username    │      │
│  │ {{ user.email }}│  [Map]       │ user.email       │      │
│  │ [unbound]       │              │ current_user.... │      │
│  │                 │              │ posts (List)     │      │
│  │ [action:submit] │──────────────│ submit_form()    │      │
│  │                 │              │ delete_post()    │      │
│  └─────────────────┘              └──────────────────┘      │
│                                                               │
│  [Auto-Suggest Bindings]  [Validate All]  [Save]            │
│                                                               │
│  Status: 3 bindings configured, 1 unbound                    │
└───────────────────────────────────────────────────────────────┘
```

### Sync Button Behavior

**Before Phase 3** (destructive):
- Overwrites entire file
- Destroys manual code

**After Phase 3** (non-destructive):
- Preserves manual code
- Updates only design changes
- Shows diff preview before applying

## Implementation Roadmap

### Milestone 1: Foundation (Week 1-2)
- [x] Create all interfaces and models
- [ ] Implement basic FigmaAnnotationParser
- [ ] Set up PSI infrastructure for introspection

### Milestone 2: Django Support (Week 3-4)
- [ ] Implement DjangoIntrospector
- [ ] Test with real Django project
- [ ] Basic DataBindingManager implementation

### Milestone 3: Sync Mechanism (Week 5-6)
- [ ] Implement code markers
- [ ] Develop merge algorithm
- [ ] Create NonDestructiveSyncService

### Milestone 4: UI Integration (Week 7-8)
- [ ] Build Data Binding tab in tool window
- [ ] Implement drag-and-drop mapping
- [ ] Add sync button with preview

### Milestone 5: Flask/FastAPI (Week 9-10)
- [ ] Implement FlaskIntrospector
- [ ] Implement FastAPIIntrospector
- [ ] Test with multiple frameworks

### Milestone 6: Polish (Week 11-12)
- [ ] Auto-suggest bindings
- [ ] Conflict resolution UI
- [ ] Documentation and examples

## Technical Challenges

### Challenge 1: PSI Tree Navigation
**Problem**: PyCharm's PSI API is complex.
**Solution**: Start with simple patterns, expand incrementally.

### Challenge 2: Type Inference
**Problem**: Python is dynamically typed.
**Solution**: Use type hints where available, heuristics otherwise.

### Challenge 3: Checksum Stability
**Problem**: Minor whitespace changes invalidate checksums.
**Solution**: Normalize code before checksum (trim, consistent spacing).

### Challenge 4: Concurrent Edits
**Problem**: User edits file while sync is running.
**Solution**: File locks, conflict detection, merge UI.

## Testing Strategy

### Unit Tests
- FigmaAnnotationParser: Pattern matching
- DataBindingManager: CRUD operations
- NonDestructiveSyncService: Merge algorithm

### Integration Tests
- Full Django project introspection
- End-to-end sync with sample project
- Multi-framework support

### Manual Testing
- Real Figma designs
- Actual Django/Flask/FastAPI projects
- Edge cases (conflicts, missing data)

## Success Criteria

Phase 3 is complete when:

1. ✅ Developer can annotate Figma design with bindings
2. ✅ Plugin discovers backend data automatically
3. ✅ UI allows easy binding configuration
4. ✅ Sync updates design without destroying manual code
5. ✅ Works with Django, Flask, and FastAPI
6. ✅ Handles edge cases gracefully

## Example End-to-End Flow

```
1. Designer in Figma:
   - Creates "User Card" component
   - Names text node: "text:{{ user.name }}"
   - Names button: "button:[action:edit_user]"

2. Developer in PyCharm:
   - Opens plugin
   - Clicks "Sync"
   - Plugin shows:
     "Found 2 binding points, discovered 45 backend data sources"
   
3. Data Binding Tab:
   - Left: {{ user.name }} [unbound]
   - Right: current_user.username [suggested]
   - Developer clicks "Map"
   
4. Sync Operation:
   - Generates: <span>{{ current_user.username }}</span>
   - Developer previously added: {% if user.is_staff %}
   - Merge preserves {% if %}, updates <span>
   
5. Result:
   - File updated with design changes
   - Manual code preserved
   - Data binding applied
```

## Next Steps

With architecture defined, implementation can proceed task-by-task:

1. **Now**: Implement FigmaAnnotationParser (simplest)
2. **Next**: Implement DjangoIntrospector (most value)
3. **Then**: Build DataBindingManager (connects them)
4. **Finally**: Implement NonDestructiveSyncService (most complex)

Each task can be developed, tested, and merged independently.
