document.addEventListener('DOMContentLoaded', () => {
  const themeToggle = document.getElementById('theme-toggle');
  const body = document.body;

  // Theme switcher
  if (themeToggle) {
    themeToggle.addEventListener('click', () => {
      body.classList.toggle('light-theme');
      body.classList.toggle('dark-theme');
    });
  }

  // Active link highlighting on scroll
  const sections = document.querySelectorAll('.doc-section');
  const navLinks = document.querySelectorAll('.sidebar-nav a');

  window.addEventListener('scroll', () => {
    let current = '';
    sections.forEach(section => {
      const sectionTop = section.offsetTop;
      if (pageYOffset >= sectionTop - 120) {
        current = section.getAttribute('id');
      }
    });

    navLinks.forEach(link => {
      link.classList.remove('active');
      if (link.getAttribute('href') === `#${current}`) {
        link.classList.add('active');
      }
    });
  });

  // Automated 'Copy' buttons for all documentation code blocks
  const codeBlocks = document.querySelectorAll('.code-block');

  codeBlocks.forEach(block => {
    // Determine language or title hint
    let lang = 'CODE';
    const textContent = block.textContent || '';
    if (textContent.includes('#!') || textContent.includes('gradle') || textContent.includes('git clone') || textContent.includes('chmod')) {
      lang = 'BASH';
    } else if (textContent.includes('-keep') || textContent.includes('-dontwarn')) {
      lang = 'PROGUARD / R8';
    } else if (textContent.includes('PaymentIntent') || textContent.includes('Webhook')) {
      lang = 'FLOW DIAGRAM';
    }

    // Check if header already exists
    let header = block.querySelector('.code-block-header');
    if (!header) {
      header = document.createElement('div');
      header.className = 'code-block-header';

      const label = document.createElement('span');
      label.className = 'code-lang-label';
      label.textContent = lang;
      header.appendChild(label);

      block.insertBefore(header, block.firstChild);
    }

    // Check if copy button already exists
    if (!block.querySelector('.btn-copy-code')) {
      const copyBtn = document.createElement('button');
      copyBtn.type = 'button';
      copyBtn.className = 'btn-copy-code';
      copyBtn.setAttribute('aria-label', 'Copy code to clipboard');
      copyBtn.innerHTML = `
        <svg class="copy-icon" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
          <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
        </svg>
        <span>Copy</span>
      `;
      header.appendChild(copyBtn);
    }
  });

  // Delegate copy button clicks
  document.addEventListener('click', (e) => {
    const btn = e.target.closest('.btn-copy-code');
    if (!btn) return;

    const block = btn.closest('.code-block');
    if (!block) return;

    const codeEl = block.querySelector('code') || block.querySelector('pre');
    if (!codeEl) return;

    const codeText = codeEl.innerText || codeEl.textContent;

    const copySuccess = () => {
      btn.classList.add('copied');
      btn.innerHTML = `
        <svg class="check-icon" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="20 6 9 17 4 12"/>
        </svg>
        <span>Copied!</span>
      `;
      setTimeout(() => {
        btn.classList.remove('copied');
        btn.innerHTML = `
          <svg class="copy-icon" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
            <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
          </svg>
          <span>Copy</span>
        `;
      }, 2000);
    };

    if (navigator.clipboard && window.isSecureContext) {
      navigator.clipboard.writeText(codeText.trim())
        .then(copySuccess)
        .catch(() => fallbackCopy(codeText.trim(), copySuccess));
    } else {
      fallbackCopy(codeText.trim(), copySuccess);
    }
  });

  function fallbackCopy(text, callback) {
    const textarea = document.createElement('textarea');
    textarea.value = text;
    textarea.style.position = 'fixed';
    textarea.style.opacity = '0';
    document.body.appendChild(textarea);
    textarea.select();
    try {
      document.execCommand('copy');
      callback();
    } catch (err) {
      console.error('Fallback copy failed', err);
    }
    document.body.removeChild(textarea);
  }
});
