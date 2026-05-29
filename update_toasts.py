import os, glob

for filepath in glob.glob('src/main/resources/templates/*.html'):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    changed = False
    
    if '<span th:text="${pass}"></span>' in content and 'toastify-close' not in content:
        content = content.replace('<span th:text="${pass}"></span>', '<span th:text="${pass}"></span>\n\t\t\t<button class="toastify-close" onclick="this.parentElement.remove()">×</button>')
        changed = True
        
    if '<span th:text="${fail}"></span>' in content and 'toastify-close' not in content:
        content = content.replace('<span th:text="${fail}"></span>', '<span th:text="${fail}"></span>\n\t\t\t<button class="toastify-close" onclick="this.parentElement.remove()">×</button>')
        changed = True
        
    # Also fix CSS for toastify to align properly
    if '.toastify {' in content:
        if 'align-items: center;' not in content:
            content = content.replace('.toastify {', '.toastify {\n\talign-items: center;\n\tjustify-content: space-between;')
            changed = True
            
    if changed:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f'Updated {filepath}')
