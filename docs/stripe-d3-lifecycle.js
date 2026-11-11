/**
 * BillingHub - Real-Time Stripe Payment Lifecycle Visualizer
 * Dynamic SVG Diagram rendered with D3.js (v7)
 * Visualizes the complete PaymentIntent, 3DS SCA challenge, Webhook telemetry,
 * and Stripe Connect live payout lifecycle with interactive particle animations.
 */

(function() {
  'use strict';

  // Wait for DOM & D3 availability
  function initVisualizer() {
    const svgEl = document.getElementById('stripe-lifecycle-svg');
    if (!svgEl || typeof d3 === 'undefined') {
      setTimeout(initVisualizer, 100);
      return;
    }

    const svg = d3.select('#stripe-lifecycle-svg');
    svg.selectAll('*').remove(); // Clear existing

    const width = 960;
    const height = 480;
    svg.attr('viewBox', `0 0 ${width} ${height}`)
       .attr('preserveAspectRatio', 'xMidYMid meet');

    // 1. Defs: Gradients, Filters, Markers
    const defs = svg.append('defs');

    // Glow filter
    const filter = defs.append('filter')
      .attr('id', 'glow-filter')
      .attr('x', '-30%')
      .attr('y', '-30%')
      .attr('width', '160%')
      .attr('height', '160%');
    filter.append('feGaussianBlur')
      .attr('stdDeviation', '4.5')
      .attr('result', 'blur');
    filter.append('feComposite')
      .attr('in', 'SourceGraphic')
      .attr('in2', 'blur')
      .attr('operator', 'over');

    // Pulse gradient for active nodes
    const gradCyan = defs.append('linearGradient')
      .attr('id', 'grad-cyan')
      .attr('x1', '0%').attr('y1', '0%')
      .attr('x2', '100%').attr('y2', '100%');
    gradCyan.append('stop').attr('offset', '0%').attr('stop-color', '#6366f1');
    gradCyan.append('stop').attr('offset', '100%').attr('stop-color', '#06b6d4');

    const gradSuccess = defs.append('linearGradient')
      .attr('id', 'grad-success')
      .attr('x1', '0%').attr('y1', '0%')
      .attr('x2', '100%').attr('y2', '100%');
    gradSuccess.append('stop').attr('offset', '0%').attr('stop-color', '#10b981');
    gradSuccess.append('stop').attr('offset', '100%').attr('stop-color', '#059669');

    // 2. Node Schema
    const nodes = [
      {
        id: 'app_checkout',
        step: '01',
        title: 'App Checkout',
        subtitle: 'Pro Tier ($29/mo)',
        x: 90,
        y: 80,
        category: 'client',
        color: '#6366f1',
        endpoint: 'Client Activity (Jetpack Compose)',
        crypto: 'TLS 1.3 / ChaCha20-Poly1305',
        payload: {
          action: "select_plan",
          plan_id: "pro_monthly",
          amount: 2900,
          currency: "usd",
          customer_id: "cus_Q1k48Xv9P2m"
        }
      },
      {
        id: 'stripe_elements',
        step: '02',
        title: 'Stripe Elements',
        subtitle: 'Zero-Knowledge Token',
        x: 320,
        y: 80,
        category: 'sdk',
        color: '#818cf8',
        endpoint: 'POST https://api.stripe.com/v1/tokens',
        crypto: 'PCI-DSS Level 1 / AES-256-GCM',
        payload: {
          type: "card",
          card_token: "tok_1Q9xZ2eZvKYlo2Cxyz",
          client_secret: "pi_1Nx..._secret_...",
          radar_session: "r_sess_90ab78f12c"
        }
      },
      {
        id: 'payment_intent',
        step: '03',
        title: 'PaymentIntent',
        subtitle: 'Confirm & Reserve',
        x: 550,
        y: 80,
        category: 'api',
        color: '#06b6d4',
        endpoint: 'POST https://api.stripe.com/v1/payment_intents/confirm',
        crypto: 'Idempotency: idem_live_9f81bc42',
        payload: {
          id: "pi_3MtwBwLkdIwHu7ix28a3tqPa",
          amount: 2900,
          currency: "usd",
          automatic_payment_methods: { enabled: true },
          capture_method: "automatic",
          status: "requires_action"
        }
      },
      {
        id: 'stripe_radar',
        step: '04',
        title: 'Radar AI Risk',
        subtitle: 'Fraud & SCA Check',
        x: 780,
        y: 80,
        category: 'risk',
        color: '#38bdf8',
        endpoint: 'Stripe Machine Learning Classifier',
        crypto: 'Risk Score: 12 / 100 (Normal)',
        payload: {
          risk_evaluation: "normal",
          risk_score: 12,
          rule_action: "allow",
          sca_required: "conditional_3ds"
        }
      },
      {
        id: 'sca_challenge',
        step: '05',
        title: '3DS Challenge',
        subtitle: 'Bank OTP Verification',
        x: 780,
        y: 220,
        category: 'auth',
        color: '#ec4899',
        endpoint: 'EMVCo 3-D Secure 2.2 Protocol',
        crypto: 'Signed Challenge JWT / RSA-SHA256',
        payload: {
          three_d_secure: {
            version: "2.2.0",
            authentication_status: "Y",
            cavv: "AAABBIIFmQAAAAAAEYWZAAAAAAA=",
            ds_trans_id: "ds_90bca78-45e"
          }
        }
      },
      {
        id: 'card_network',
        step: '06',
        title: 'Card Network',
        subtitle: 'Visa / Issuer Capture',
        x: 550,
        y: 220,
        category: 'network',
        color: '#10b981',
        endpoint: 'Visa Direct / Mastercard Settlement',
        crypto: 'Auth Code: 089421 / Capture Succeeded',
        payload: {
          charge_id: "ch_3MtwBwLkdIwHu7ix28a3tqPa",
          network: "visa",
          captured: true,
          seller_message: "Payment complete."
        }
      },
      {
        id: 'webhook_dispatch',
        step: '07',
        title: 'Stripe Webhook',
        subtitle: 'HMAC-SHA256 Dispatch',
        x: 320,
        y: 220,
        category: 'webhook',
        color: '#f59e0b',
        endpoint: 'POST https://api.billinghub.io/webhooks/stripe',
        crypto: 'Stripe-Signature: t=1790878200,v1=5257a869e7...',
        payload: {
          id: "evt_1NxABC2eZvKYlo2Cxyz12345",
          type: "invoice.payment_succeeded",
          data: {
            object: {
              customer: "cus_Q1k48Xv9P2m",
              subscription: "sub_1NxProSubscription",
              amount_paid: 2900,
              status: "paid"
            }
          }
        }
      },
      {
        id: 'quota_provision',
        step: '08',
        title: 'App Entitlement',
        subtitle: 'Quota + 150K Reqs',
        x: 90,
        y: 220,
        category: 'app',
        color: '#10b981',
        endpoint: 'BillingHub Client Sync (DataStore / Room)',
        crypto: 'Keystore HSM AES-256-GCM local sign',
        payload: {
          tier: "PRO",
          status: "ACTIVE",
          monthly_quota: 150000,
          used_requests: 3420,
          remaining_requests: 146580,
          renews_at: "2026-12-01T00:00:00Z"
        }
      },
      {
        id: 'live_payout',
        step: '09',
        title: 'Live Connect Payout',
        subtitle: 'ACH / Instant Transfer',
        x: 440,
        y: 360,
        category: 'payout',
        color: '#a855f7',
        endpoint: 'POST https://api.stripe.com/v1/payouts',
        crypto: 'FedNow / Visa Direct Instant Settlement',
        payload: {
          id: "po_1Q4B39KlzP98Xy79mX8q",
          amount: 485000,
          currency: "usd",
          destination: "ba_1Q4B39KlzP98Chase",
          arrival_date: "Instant (< 30 mins)",
          status: "paid"
        }
      }
    ];

    // Node lookup map
    const nodeMap = {};
    nodes.forEach(n => { nodeMap[n.id] = n; });

    // 3. Edges / Transitions
    const edges = [
      { id: 'e1', from: 'app_checkout', to: 'stripe_elements', type: 'standard' },
      { id: 'e2', from: 'stripe_elements', to: 'payment_intent', type: 'standard' },
      { id: 'e3', from: 'payment_intent', to: 'stripe_radar', type: 'standard' },
      { id: 'e4_direct', from: 'stripe_radar', to: 'card_network', type: 'standard' },
      { id: 'e4_sca', from: 'stripe_radar', to: 'sca_challenge', type: 'sca' },
      { id: 'e5_sca', from: 'sca_challenge', to: 'card_network', type: 'sca' },
      { id: 'e6', from: 'card_network', to: 'webhook_dispatch', type: 'standard' },
      { id: 'e7', from: 'webhook_dispatch', to: 'quota_provision', type: 'standard' },
      { id: 'e8', from: 'quota_provision', to: 'live_payout', type: 'payout' }
    ];

    // Helper: Build smooth bezier path between nodes
    function buildPath(source, target, type) {
      const sx = source.x + 85;
      const sy = source.y + 30;
      const tx = target.x + 85;
      const ty = target.y + 30;

      if (type === 'standard' && sy === ty) {
        // Horizontal forward
        const mx = (sx + tx) / 2;
        return `M ${sx} ${sy} L ${tx} ${ty}`;
      } else if (source.id === 'stripe_radar' && target.id === 'card_network') {
        // Direct curve skipping SCA
        return `M ${sx} ${sy} C ${sx + 40} ${sy + 40}, ${tx + 40} ${ty - 40}, ${tx} ${ty}`;
      } else if (source.id === 'quota_provision' && target.id === 'live_payout') {
        // Loop down to Live Payout
        return `M ${sx} ${sy + 30} C ${sx} ${ty}, ${tx - 80} ${ty}, ${tx} ${ty}`;
      } else {
        const dx = (tx - sx) / 2;
        const dy = (ty - sy) / 2;
        return `M ${sx} ${sy} C ${sx + dx} ${sy}, ${tx - dx} ${ty}, ${tx} ${ty}`;
      }
    }

    // 4. Render SVG Links Layer
    const linksGroup = svg.append('g').attr('class', 'svg-links-layer');
    const edgePaths = {};

    edges.forEach(edge => {
      const src = nodeMap[edge.from];
      const tgt = nodeMap[edge.to];
      const d = buildPath(src, tgt, edge.type);

      const pathClass = `svg-edge ${edge.type === 'sca' ? 'sca-edge' : ''} ${edge.type === 'payout' ? 'payout-edge' : ''}`;
      const pathEl = linksGroup.append('path')
        .attr('id', `path-${edge.id}`)
        .attr('class', pathClass)
        .attr('d', d);

      edgePaths[edge.id] = pathEl;
    });

    // 5. Render SVG Nodes Layer
    const nodesGroup = svg.append('g').attr('class', 'svg-nodes-layer');
    const nodeGroups = {};

    nodes.forEach(node => {
      const g = nodesGroup.append('g')
        .attr('id', `node-${node.id}`)
        .attr('class', 'svg-node-group')
        .attr('transform', `translate(${node.x}, ${node.y})`);

      // Node Card Background
      g.append('rect')
        .attr('class', 'svg-node-bg')
        .attr('width', 170)
        .attr('height', 62);

      // Step Tag
      g.append('rect')
        .attr('x', 8)
        .attr('y', 8)
        .attr('width', 22)
        .attr('height', 16)
        .attr('rx', 4)
        .attr('fill', 'rgba(255,255,255,0.08)');

      g.append('text')
        .attr('x', 19)
        .attr('y', 20)
        .attr('text-anchor', 'middle')
        .attr('fill', node.color)
        .attr('font-size', '9px')
        .attr('font-weight', '700')
        .attr('font-family', 'var(--font-mono)')
        .text(node.step);

      // Title
      g.append('text')
        .attr('x', 36)
        .attr('y', 21)
        .attr('fill', '#f8fafc')
        .attr('font-size', '12px')
        .attr('font-weight', '700')
        .text(node.title);

      // Subtitle
      g.append('text')
        .attr('x', 12)
        .attr('y', 44)
        .attr('fill', '#94a3b8')
        .attr('font-size', '10px')
        .attr('font-family', 'var(--font-mono)')
        .text(node.subtitle);

      // Interactive hover & click
      g.on('mouseenter', () => selectNode(node));
      g.on('click', () => selectNode(node));

      nodeGroups[node.id] = g;
    });

    // 6. Packet Particle Layer
    const packetGroup = svg.append('g').attr('class', 'svg-packet-layer');

    // 7. Node Inspector & Console Logic
    const inspectorTitle = document.getElementById('d3-inspector-title');
    const inspectorEndpoint = document.getElementById('d3-inspector-endpoint');
    const inspectorCrypto = document.getElementById('d3-inspector-crypto');
    const inspectorCode = document.getElementById('d3-inspector-code');
    const logConsole = document.getElementById('d3-log-console');
    const statusVal = document.getElementById('d3-metric-status');
    const latencyVal = document.getElementById('d3-metric-latency');
    const idemVal = document.getElementById('d3-metric-idem');
    const webhookVal = document.getElementById('d3-metric-webhook');

    function selectNode(node) {
      if (inspectorTitle) inspectorTitle.textContent = `${node.step}. ${node.title}`;
      if (inspectorEndpoint) inspectorEndpoint.textContent = node.endpoint;
      if (inspectorCrypto) inspectorCrypto.textContent = node.crypto;
      if (inspectorCode) inspectorCode.textContent = JSON.stringify(node.payload, null, 2);
    }

    function appendLog(level, tag, message) {
      if (!logConsole) return;
      const now = new Date();
      const timeStr = now.toTimeString().split(' ')[0] + '.' + String(now.getMilliseconds()).padStart(3, '0');
      const entry = document.createElement('div');
      entry.className = 'd3-log-entry';
      entry.innerHTML = `
        <span class="d3-log-time">${timeStr}</span>
        <span class="d3-log-tag ${level}">${tag}</span>
        <span>${message}</span>
      `;
      logConsole.appendChild(entry);
      logConsole.scrollTop = logConsole.scrollHeight;
    }

    function resetSimulation() {
      // Clear all active classes
      Object.values(nodeGroups).forEach(g => {
        g.classed('active', false).classed('completed', false).classed('failed', false);
      });
      Object.values(edgePaths).forEach(p => {
        p.classed('active', false);
      });
      packetGroup.selectAll('*').remove();

      if (statusVal) {
        statusVal.textContent = 'READY / IDLE';
        statusVal.className = 'd3-metric-val status-ready';
      }
      if (latencyVal) latencyVal.textContent = '0ms';
      if (idemVal) idemVal.textContent = 'idem_ready_none';
      if (webhookVal) webhookVal.textContent = 'Awaiting Event';

      appendLog('info', 'RESET', 'Lifecycle diagram reset to idle state.');
      selectNode(nodes[0]);
    }

    // Particle Animation Along SVG Path
    function animatePacket(pathEl, duration, color, onComplete) {
      const pathNode = pathEl.node();
      if (!pathNode) {
        if (onComplete) onComplete();
        return;
      }

      const pathLength = pathNode.getTotalLength();
      const particle = packetGroup.append('circle')
        .attr('r', 6)
        .attr('fill', color || '#38bdf8')
        .style('filter', 'url(#glow-filter)');

      const trail = packetGroup.append('circle')
        .attr('r', 10)
        .attr('fill', color || '#38bdf8')
        .attr('opacity', 0.35);

      const startTime = performance.now();

      function step(now) {
        const elapsed = now - startTime;
        const t = Math.min(elapsed / duration, 1);
        const point = pathNode.getPointAtLength(t * pathLength);

        particle.attr('cx', point.x).attr('cy', point.y);
        trail.attr('cx', point.x).attr('cy', point.y);

        if (t < 1) {
          requestAnimationFrame(step);
        } else {
          particle.remove();
          trail.remove();
          if (onComplete) onComplete();
        }
      }

      requestAnimationFrame(step);
    }

    // Sequence Player
    let isRunning = false;

    function runSimulation(flowType) {
      if (isRunning) return;
      isRunning = true;
      resetSimulation();

      const startTime = performance.now();

      function updateLatency() {
        if (!isRunning) return;
        const currentMs = Math.round(performance.now() - startTime);
        if (latencyVal) latencyVal.textContent = `${currentMs}ms`;
        requestAnimationFrame(updateLatency);
      }
      requestAnimationFrame(updateLatency);

      if (flowType === 'sca') {
        runScaFlow(() => { isRunning = false; });
      } else if (flowType === 'payout') {
        runPayoutFlow(() => { isRunning = false; });
      } else if (flowType === 'fail') {
        runFailFlow(() => { isRunning = false; });
      } else if (flowType === 'pause') {
        runPauseFlow(() => { isRunning = false; });
      } else {
        runStandardFlow(() => { isRunning = false; });
      }
    }

    // Flow 1: Standard Payment Lifecycle
    function runStandardFlow(done) {
      if (statusVal) {
        statusVal.textContent = 'PROCESSING';
        statusVal.className = 'd3-metric-val status-active';
      }
      if (idemVal) idemVal.textContent = 'idem_live_9f81bc42';

      appendLog('info', 'CHECKOUT', 'User initialized Pro tier checkout ($29/mo).');
      nodeGroups['app_checkout'].classed('active', true);
      selectNode(nodeMap['app_checkout']);

      setTimeout(() => {
        nodeGroups['app_checkout'].classed('active', false).classed('completed', true);
        edgePaths['e1'].classed('active', true);
        appendLog('info', 'TOKENIZE', 'Stripe Elements collected card & generated PCI token tok_1Q9x...');

        animatePacket(edgePaths['e1'], 550, '#38bdf8', () => {
          nodeGroups['stripe_elements'].classed('active', true);
          selectNode(nodeMap['stripe_elements']);

          setTimeout(() => {
            nodeGroups['stripe_elements'].classed('active', false).classed('completed', true);
            edgePaths['e2'].classed('active', true);
            appendLog('info', 'INTENT', 'PaymentIntent created (pi_3MtwBw...) status=confirming');

            animatePacket(edgePaths['e2'], 550, '#06b6d4', () => {
              nodeGroups['payment_intent'].classed('active', true);
              selectNode(nodeMap['payment_intent']);

              setTimeout(() => {
                nodeGroups['payment_intent'].classed('active', false).classed('completed', true);
                edgePaths['e3'].classed('active', true);
                appendLog('info', 'RADAR', 'Stripe Radar ML evaluated risk score: 12/100 (Frictionless)');

                animatePacket(edgePaths['e3'], 550, '#38bdf8', () => {
                  nodeGroups['stripe_radar'].classed('active', true);
                  selectNode(nodeMap['stripe_radar']);

                  setTimeout(() => {
                    nodeGroups['stripe_radar'].classed('active', false).classed('completed', true);
                    edgePaths['e4_direct'].classed('active', true);
                    appendLog('success', 'CAPTURE', 'Visa network approved charge. Succeeded.');

                    animatePacket(edgePaths['e4_direct'], 650, '#10b981', () => {
                      nodeGroups['card_network'].classed('active', true);
                      selectNode(nodeMap['card_network']);

                      setTimeout(() => {
                        nodeGroups['card_network'].classed('active', false).classed('completed', true);
                        edgePaths['e6'].classed('active', true);
                        if (webhookVal) webhookVal.textContent = 'invoice.payment_succeeded';
                        appendLog('info', 'WEBHOOK', 'Dispatched signed HMAC-SHA256 webhook: invoice.payment_succeeded');

                        animatePacket(edgePaths['e6'], 550, '#f59e0b', () => {
                          nodeGroups['webhook_dispatch'].classed('active', true);
                          selectNode(nodeMap['webhook_dispatch']);

                          setTimeout(() => {
                            nodeGroups['webhook_dispatch'].classed('active', false).classed('completed', true);
                            edgePaths['e7'].classed('active', true);
                            appendLog('success', 'QUOTA', 'BillingHub unlocked 150,000 monthly API quota.');

                            animatePacket(edgePaths['e7'], 550, '#10b981', () => {
                              nodeGroups['quota_provision'].classed('active', true);
                              selectNode(nodeMap['quota_provision']);

                              if (statusVal) {
                                statusVal.textContent = 'SETTLED & ACTIVE';
                                statusVal.className = 'd3-metric-val status-ready';
                              }
                              appendLog('success', 'COMPLETE', 'Standard payment cycle complete in 312ms.');
                              done();
                            });
                          }, 350);
                        });
                      }, 350);
                    });
                  }, 350);
                });
              }, 350);
            });
          }, 350);
        });
      }, 350);
    }

    // Flow 2: 3D Secure (SCA Challenge) Flow
    function runScaFlow(done) {
      if (statusVal) {
        statusVal.textContent = '3DS CHALLENGE';
        statusVal.className = 'd3-metric-val status-sca';
      }
      if (idemVal) idemVal.textContent = 'idem_sca_challenge_77b';

      appendLog('warning', '3DS', 'Simulating card requiring Strong Customer Authentication (SCA)...');
      nodeGroups['app_checkout'].classed('completed', true);
      nodeGroups['stripe_elements'].classed('completed', true);
      nodeGroups['payment_intent'].classed('completed', true);
      nodeGroups['stripe_radar'].classed('active', true);
      selectNode(nodeMap['stripe_radar']);

      setTimeout(() => {
        nodeGroups['stripe_radar'].classed('active', false).classed('completed', true);
        edgePaths['e4_sca'].classed('active', true);
        appendLog('warning', 'SCA', 'Radar flagged High Value / Regulatory 3DS requirement.');

        animatePacket(edgePaths['e4_sca'], 600, '#ec4899', () => {
          nodeGroups['sca_challenge'].classed('active', true);
          selectNode(nodeMap['sca_challenge']);
          appendLog('info', 'OTP', 'Presenting Bank OTP authentication challenge modal...');

          setTimeout(() => {
            appendLog('success', 'VERIFIED', 'Customer entered valid OTP (Status: Y, CAVV generated).');
            nodeGroups['sca_challenge'].classed('active', false).classed('completed', true);
            edgePaths['e5_sca'].classed('active', true);

            animatePacket(edgePaths['e5_sca'], 600, '#10b981', () => {
              nodeGroups['card_network'].classed('active', true).classed('completed', true);
              selectNode(nodeMap['card_network']);
              edgePaths['e6'].classed('active', true);

              animatePacket(edgePaths['e6'], 500, '#f59e0b', () => {
                nodeGroups['webhook_dispatch'].classed('completed', true);
                edgePaths['e7'].classed('active', true);

                animatePacket(edgePaths['e7'], 500, '#10b981', () => {
                  nodeGroups['quota_provision'].classed('active', true);
                  selectNode(nodeMap['quota_provision']);
                  if (statusVal) {
                    statusVal.textContent = 'SCA VERIFIED & SETTLED';
                    statusVal.className = 'd3-metric-val status-ready';
                  }
                  if (webhookVal) webhookVal.textContent = 'invoice.payment_succeeded';
                  appendLog('success', 'COMPLETE', '3D Secure challenge successfully authenticated.');
                  done();
                });
              });
            });
          }, 800);
        });
      }, 400);
    }

    // Flow 3: Live Stripe Connect Payout Flow
    function runPayoutFlow(done) {
      if (statusVal) {
        statusVal.textContent = 'PAYOUT IN-TRANSIT';
        statusVal.className = 'd3-metric-val status-active';
      }
      if (idemVal) idemVal.textContent = 'idem_payout_live_449';

      appendLog('payout', 'PAYOUT', 'Initiating live Stripe Connect payout: $4,850.00 USD');
      nodeGroups['quota_provision'].classed('completed', true);
      selectNode(nodeMap['quota_provision']);

      edgePaths['e8'].classed('active', true);
      animatePacket(edgePaths['e8'], 700, '#a855f7', () => {
        nodeGroups['live_payout'].classed('active', true);
        selectNode(nodeMap['live_payout']);

        if (webhookVal) webhookVal.textContent = 'payout.created';
        appendLog('payout', 'IN-TRANSIT', 'ACH / Visa Direct transfer dispatched to Chase Bank (*8821).');

        setTimeout(() => {
          if (webhookVal) webhookVal.textContent = 'payout.paid';
          if (statusVal) {
            statusVal.textContent = 'PAYOUT PAID';
            statusVal.className = 'd3-metric-val status-ready';
          }
          nodeGroups['live_payout'].classed('active', false).classed('completed', true);
          appendLog('success', 'PAID', 'Webhook payout.paid received. Net $4,777.25 deposited.');
          done();
        }, 800);
      });
    }

    // Flow 4: Failure & Grace Period Flow
    function runFailFlow(done) {
      if (statusVal) {
        statusVal.textContent = 'CARD DECLINED';
        statusVal.className = 'd3-metric-val status-fail';
      }
      if (idemVal) idemVal.textContent = 'idem_fail_sim_000';

      appendLog('error', 'DECLINED', 'Simulating card declined: 4000 0000 0000 0002');
      nodeGroups['app_checkout'].classed('completed', true);
      nodeGroups['stripe_elements'].classed('completed', true);
      nodeGroups['payment_intent'].classed('active', true);
      selectNode(nodeMap['payment_intent']);

      setTimeout(() => {
        nodeGroups['payment_intent'].classed('active', false).classed('failed', true);
        if (webhookVal) webhookVal.textContent = 'invoice.payment_failed';
        appendLog('error', 'FAILED', 'Stripe Error: card_declined (insufficient_funds).');
        appendLog('warning', 'GRACE', 'Smart Retries triggered: 3-day SaaS grace period initiated.');
        done();
      }, 700);
    }

    // Flow 5: Pause Subscription & Webhook Invoicing Freeze
    function runPauseFlow(done) {
      if (statusVal) {
        statusVal.textContent = 'SUBSCRIPTION PAUSED';
        statusVal.className = 'd3-metric-val status-active';
      }
      if (idemVal) idemVal.textContent = 'idem_pause_sub_992a';
      if (webhookVal) webhookVal.textContent = 'customer.subscription.paused';

      appendLog('warning', 'PAUSE', 'User requested 4-week subscription hold. Triggering Stripe API...');
      nodeGroups['app_checkout'].classed('active', true);

      const pausePayloadNode = {
        step: '07',
        title: 'Webhook: customer.subscription.paused',
        endpoint: 'POST https://api.billinghub.io/webhooks/stripe',
        crypto: 'Stripe-Signature: t=1790878200,v1=90a8fe42bc...',
        payload: {
          id: "evt_1Q5vPause998Klz",
          object: "event",
          type: "customer.subscription.paused",
          data: {
            object: {
              id: "sub_1Q5vY3KlzP923nmX81",
              customer: "cus_R9x2Kl84mQpZ01",
              status: "paused",
              pause_collection: {
                behavior: "void",
                resumes_at: 1794508800
              }
            }
          }
        }
      };

      setTimeout(() => {
        nodeGroups['app_checkout'].classed('active', false).classed('completed', true);
        edgePaths['e1'].classed('active', true);
        edgePaths['e2'].classed('active', true);
        appendLog('info', 'STRIPE', 'Stripe set collection behavior: void. Dispatching webhook broadcast...');

        nodeGroups['webhook_dispatch'].classed('active', true);
        selectNode(pausePayloadNode);

        setTimeout(() => {
          nodeGroups['webhook_dispatch'].classed('active', false).classed('completed', true);
          edgePaths['e7'].classed('active', true);
          appendLog('success', 'WEBHOOK', 'Webhook delivered (200 OK). Billing state updated to Paused.');

          animatePacket(edgePaths['e7'], 600, '#fbbf24', () => {
            nodeGroups['quota_provision'].classed('active', true);
            appendLog('warning', 'FROZEN', 'Automated recurring invoicing frozen. Quota remains accessible.');
            if (statusVal) {
              statusVal.textContent = 'PAUSED (INVOICES VOID)';
              statusVal.className = 'd3-metric-val status-sca';
            }
            done();
          });
        }, 500);
      }, 400);
    }

    // 8. Bind Control Buttons
    const btnSimulate = document.getElementById('btn-sim-standard');
    const btnSca = document.getElementById('btn-sim-sca');
    const btnPayout = document.getElementById('btn-sim-payout');
    const btnFail = document.getElementById('btn-sim-fail');
    const btnPause = document.getElementById('btn-sim-pause');
    const btnReset = document.getElementById('btn-sim-reset');

    if (btnSimulate) btnSimulate.addEventListener('click', () => runSimulation('standard'));
    if (btnSca) btnSca.addEventListener('click', () => runSimulation('sca'));
    if (btnPayout) btnPayout.addEventListener('click', () => runSimulation('payout'));
    if (btnFail) btnFail.addEventListener('click', () => runSimulation('fail'));
    if (btnPause) btnPause.addEventListener('click', () => runSimulation('pause'));
    if (btnReset) btnReset.addEventListener('click', resetSimulation);

    // Initial default selection
    selectNode(nodes[0]);
    appendLog('info', 'READY', 'D3.js SVG Payment Lifecycle visualizer initialized.');
  }

  // Trigger setup on DOM ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initVisualizer);
  } else {
    initVisualizer();
  }
})();
