import { useEffect, useState } from "react";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";
const tabs = ["dashboard", "products", "customers", "billing", "reports"];

const initialProductForm = {
  name: "",
  brand: "",
  category: "",
  sku: "",
  packSize: "",
  unit: "bag",
  batchNumber: "",
  expiryDate: "",
  purchasePrice: "",
  salePrice: "",
  stockQuantity: "",
  lowStockThreshold: "",
  supplierName: "",
};

const initialCustomerForm = {
  name: "",
  phone: "",
  villageOrAddress: "",
  notes: "",
};

const initialSaleForm = {
  customerId: "",
  discount: "0",
  paidAmount: "0",
  paymentMethod: "CASH",
  note: "",
  items: [{ productId: "", quantity: 1 }],
};

function HomePage({ session, onLogout }) {
  const [activeTab, setActiveTab] = useState("dashboard");
  const [summary, setSummary] = useState(null);
  const [products, setProducts] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [sales, setSales] = useState([]);
  const [reports, setReports] = useState({ sales: [], dues: [], lowStock: [] });
  const [selectedCustomerId, setSelectedCustomerId] = useState(null);
  const [ledger, setLedger] = useState(null);
  const [productForm, setProductForm] = useState(initialProductForm);
  const [customerForm, setCustomerForm] = useState(initialCustomerForm);
  const [saleForm, setSaleForm] = useState(initialSaleForm);
  const [paymentForm, setPaymentForm] = useState({ amount: "", paymentMethod: "CASH", note: "" });
  const [latestSale, setLatestSale] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [banner, setBanner] = useState({ type: "", text: "" });

  useEffect(() => {
    loadAllData();
  }, []);

  useEffect(() => {
    if (selectedCustomerId) {
      loadLedger(selectedCustomerId);
    }
  }, [selectedCustomerId]);

  async function request(path, options = {}) {
    const response = await fetch(`${API_BASE}${path}`, options);
    const data = response.status === 204 ? null : await response.json();
    if (!response.ok) {
      throw new Error(data?.message || "Request failed.");
    }
    return data;
  }

  async function loadAllData() {
    setLoading(true);
    try {
      const [summaryData, productData, customerData, salesData, reportSales, reportDues, reportLowStock] =
        await Promise.all([
          request("/dashboard/summary"),
          request("/products"),
          request("/customers"),
          request("/sales"),
          request("/reports/sales"),
          request("/reports/dues"),
          request("/reports/low-stock"),
        ]);

      setSummary(summaryData);
      setProducts(productData);
      setCustomers(customerData);
      setSales(salesData);
      setReports({ sales: reportSales, dues: reportDues, lowStock: reportLowStock });
      if (!selectedCustomerId && customerData.length > 0) {
        setSelectedCustomerId(customerData[0].id);
      }
      setBanner({ type: "", text: "" });
    } catch (error) {
      setBanner({ type: "error", text: error.message });
    } finally {
      setLoading(false);
    }
  }

  async function loadLedger(customerId) {
    try {
      setLedger(await request(`/customers/${customerId}/ledger`));
    } catch (error) {
      setBanner({ type: "error", text: error.message });
    }
  }

  async function handleProductSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    try {
      await request("/products", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          ...productForm,
          purchasePrice: Number(productForm.purchasePrice),
          salePrice: Number(productForm.salePrice),
          stockQuantity: Number(productForm.stockQuantity),
          lowStockThreshold: Number(productForm.lowStockThreshold),
        }),
      });
      setProductForm(initialProductForm);
      await loadAllData();
      setBanner({ type: "success", text: "Product created successfully." });
    } catch (error) {
      setBanner({ type: "error", text: error.message });
    } finally {
      setSubmitting(false);
    }
  }

  async function handleCustomerSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    try {
      const createdCustomer = await request("/customers", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(customerForm),
      });
      setCustomerForm(initialCustomerForm);
      await loadAllData();
      setSelectedCustomerId(createdCustomer.id);
      setBanner({ type: "success", text: "Customer saved successfully." });
    } catch (error) {
      setBanner({ type: "error", text: error.message });
    } finally {
      setSubmitting(false);
    }
  }

  async function handlePaymentSubmit(event) {
    event.preventDefault();
    if (!selectedCustomerId) {
      return;
    }
    setSubmitting(true);
    try {
      await request(`/customers/${selectedCustomerId}/payments`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          ...paymentForm,
          amount: Number(paymentForm.amount),
          recordedBy: session.userId,
        }),
      });
      setPaymentForm({ amount: "", paymentMethod: "CASH", note: "" });
      await loadAllData();
      await loadLedger(selectedCustomerId);
      setBanner({ type: "success", text: "Payment recorded successfully." });
    } catch (error) {
      setBanner({ type: "error", text: error.message });
    } finally {
      setSubmitting(false);
    }
  }

  async function handleStockAdjustment(productId, adjustmentType) {
    const quantityText = window.prompt(`Enter quantity to ${adjustmentType === "ADD" ? "add" : "remove"}:`);
    if (!quantityText) {
      return;
    }
    const quantity = Number(quantityText);
    if (!quantity || quantity < 0) {
      setBanner({ type: "error", text: "Enter a valid quantity." });
      return;
    }

    try {
      await request(`/products/${productId}/stock`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          quantity,
          adjustmentType,
          reason: adjustmentType === "ADD" ? "Stock refill" : "Sale correction",
          createdBy: session.userId,
        }),
      });
      await loadAllData();
      setBanner({ type: "success", text: "Stock updated." });
    } catch (error) {
      setBanner({ type: "error", text: error.message });
    }
  }

  function updateSaleLine(index, field, value) {
    setSaleForm((current) => ({
      ...current,
      items: current.items.map((item, itemIndex) =>
        itemIndex === index ? { ...item, [field]: field === "quantity" ? Number(value) : value } : item
      ),
    }));
  }

  function addSaleLine() {
    setSaleForm((current) => ({
      ...current,
      items: [...current.items, { productId: "", quantity: 1 }],
    }));
  }

  function removeSaleLine(index) {
    setSaleForm((current) => ({
      ...current,
      items: current.items.filter((_, itemIndex) => itemIndex !== index),
    }));
  }

  async function handleSaleSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    try {
      const createdSale = await request("/sales", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          customerId: saleForm.customerId ? Number(saleForm.customerId) : null,
          discount: Number(saleForm.discount || 0),
          paidAmount: Number(saleForm.paidAmount || 0),
          paymentMethod: saleForm.paymentMethod,
          note: saleForm.note,
          createdBy: session.userId,
          items: saleForm.items
            .filter((item) => item.productId)
            .map((item) => ({ productId: Number(item.productId), quantity: Number(item.quantity) })),
        }),
      });
      setLatestSale(createdSale.sale);
      setSaleForm(initialSaleForm);
      await loadAllData();
      if (selectedCustomerId) {
        await loadLedger(selectedCustomerId);
      }
      setBanner({ type: "success", text: "Bill created successfully." });
    } catch (error) {
      setBanner({ type: "error", text: error.message });
    } finally {
      setSubmitting(false);
    }
  }

  async function loadSalesReportWithRange(event) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const from = form.get("from");
    const to = form.get("to");
    try {
      const salesReport = await request(`/reports/sales?from=${from}&to=${to}`);
      setReports((current) => ({ ...current, sales: salesReport }));
      setBanner({ type: "success", text: "Report updated." });
    } catch (error) {
      setBanner({ type: "error", text: error.message });
    }
  }

  const salePreview = saleForm.items.reduce(
    (result, item) => {
      const product = products.find((entry) => String(entry.id) === String(item.productId));
      const lineTotal = product ? Number(product.salePrice) * Number(item.quantity || 0) : 0;
      result.subtotal += lineTotal;
      return result;
    },
    { subtotal: 0 }
  );
  salePreview.total = Math.max(salePreview.subtotal - Number(saleForm.discount || 0), 0);
  salePreview.due = Math.max(salePreview.total - Number(saleForm.paidAmount || 0), 0);

  return (
    <main className="app-shell">
      <header className="topbar">
        <div>
          <div className="brand-chip">Fertilizer Shop Manager</div>
          <h1>Welcome back, {session.fullName}</h1>
          <p className="muted-copy">Role: {session.role} · Inventory, dues, billing, and reports in one workspace.</p>
        </div>
        <div className="topbar-actions">
          {tabs.map((tab) => (
            <button
              key={tab}
              className={tab === activeTab ? "nav-button active" : "nav-button"}
              onClick={() => setActiveTab(tab)}
              type="button"
            >
              {tab}
            </button>
          ))}
          <button className="danger-button" onClick={onLogout} type="button">
            Logout
          </button>
        </div>
      </header>

      {banner.text && <div className={`banner ${banner.type}`}>{banner.text}</div>}

      {loading ? (
        <section className="panel"><p>Loading store data...</p></section>
      ) : (
        <>
          {activeTab === "dashboard" && summary && (
            <section className="dashboard-grid">
              <article className="metric-card"><span>Today&apos;s sales</span><strong>{summary.todaySalesCount}</strong></article>
              <article className="metric-card"><span>Today&apos;s revenue</span><strong>Rs {Number(summary.todayRevenue).toFixed(2)}</strong></article>
              <article className="metric-card"><span>Outstanding due</span><strong>Rs {Number(summary.outstandingDue).toFixed(2)}</strong></article>
              <article className="metric-card"><span>Low-stock alerts</span><strong>{summary.lowStockCount}</strong></article>
              <section className="panel wide">
                <div className="section-heading"><div><p className="eyebrow">Alerts</p><h2>Low-stock products</h2></div></div>
                <div className="table-wrap"><table><thead><tr><th>Product</th><th>Batch</th><th>Stock</th><th>Threshold</th></tr></thead><tbody>
                  {summary.lowStockProducts.length === 0 ? <tr><td colSpan="4">No low-stock items right now.</td></tr> : summary.lowStockProducts.map((product) => (
                    <tr key={product.id}><td>{product.name}</td><td>{product.batchNumber}</td><td>{product.stockQuantity}</td><td>{product.lowStockThreshold}</td></tr>
                  ))}
                </tbody></table></div>
              </section>
              <section className="panel wide">
                <div className="section-heading"><div><p className="eyebrow">Sales</p><h2>Recent bills</h2></div></div>
                <div className="table-wrap"><table><thead><tr><th>Bill</th><th>Customer</th><th>Total</th><th>Status</th></tr></thead><tbody>
                  {summary.recentSales.length === 0 ? <tr><td colSpan="4">No sales yet.</td></tr> : summary.recentSales.map((sale) => (
                    <tr key={sale.id}><td>{sale.billNumber}</td><td>{sale.customerName}</td><td>Rs {Number(sale.grandTotal).toFixed(2)}</td><td>{sale.paymentStatus}</td></tr>
                  ))}
                </tbody></table></div>
              </section>
            </section>
          )}

          {activeTab === "products" && (
            <section className="workspace-grid">
              <section className="panel form-panel">
                <div className="section-heading"><div><p className="eyebrow">Inventory</p><h2>Add fertilizer product</h2></div></div>
                <form className="form-grid" onSubmit={handleProductSubmit}>
                  {Object.entries(initialProductForm).map(([key]) => (
                    <label key={key}>
                      {formatLabel(key)}
                      <input
                        required={key !== "expiryDate"}
                        type={key === "expiryDate" ? "date" : isNumericField(key) ? "number" : "text"}
                        step={isPriceField(key) ? "0.01" : "1"}
                        value={productForm[key]}
                        onChange={(event) => setProductForm({ ...productForm, [key]: event.target.value })}
                      />
                    </label>
                  ))}
                  <button className="primary-button stretch" disabled={submitting} type="submit">Save product</button>
                </form>
              </section>
              <section className="panel">
                <div className="section-heading"><div><p className="eyebrow">Stock</p><h2>Current inventory</h2></div></div>
                <div className="table-wrap"><table><thead><tr><th>Name</th><th>Batch</th><th>Expiry</th><th>Stock</th><th>Sale price</th><th>Actions</th></tr></thead><tbody>
                  {products.map((product) => (
                    <tr key={product.id}>
                      <td><strong>{product.name}</strong><div className="cell-subtext">{product.brand}</div></td>
                      <td>{product.batchNumber}</td>
                      <td>{product.expiryDate || "-"}</td>
                      <td className={product.stockQuantity <= product.lowStockThreshold ? "text-alert" : ""}>{product.stockQuantity}</td>
                      <td>Rs {Number(product.salePrice).toFixed(2)}</td>
                      <td className="table-actions">
                        <button className="ghost-button" onClick={() => handleStockAdjustment(product.id, "ADD")} type="button">Add</button>
                        <button className="ghost-button" onClick={() => handleStockAdjustment(product.id, "REMOVE")} type="button">Remove</button>
                      </td>
                    </tr>
                  ))}
                </tbody></table></div>
              </section>
            </section>
          )}

          {activeTab === "customers" && (
            <section className="workspace-grid">
              <section className="panel form-panel">
                <div className="section-heading"><div><p className="eyebrow">Customers</p><h2>Create customer profile</h2></div></div>
                <form className="form-grid" onSubmit={handleCustomerSubmit}>
                  {Object.keys(initialCustomerForm).map((key) => (
                    <label key={key}>
                      {formatLabel(key)}
                      {key === "notes" ? (
                        <textarea rows="4" value={customerForm[key]} onChange={(event) => setCustomerForm({ ...customerForm, [key]: event.target.value })} />
                      ) : (
                        <input required={key !== "notes"} value={customerForm[key]} onChange={(event) => setCustomerForm({ ...customerForm, [key]: event.target.value })} />
                      )}
                    </label>
                  ))}
                  <button className="primary-button stretch" disabled={submitting} type="submit">Save customer</button>
                </form>
              </section>
              <section className="panel">
                <div className="section-heading"><div><p className="eyebrow">Ledger</p><h2>Customer dues and history</h2></div></div>
                <label className="select-label">
                  Select customer
                  <select value={selectedCustomerId || ""} onChange={(event) => setSelectedCustomerId(Number(event.target.value))}>
                    {customers.map((customer) => <option key={customer.id} value={customer.id}>{customer.name} - {customer.phone}</option>)}
                  </select>
                </label>
                {ledger && (
                  <>
                    <div className="ledger-summary">
                      <article><span>Current due</span><strong>Rs {Number(ledger.customer.currentDue).toFixed(2)}</strong></article>
                      <article><span>Village / Address</span><strong>{ledger.customer.villageOrAddress}</strong></article>
                    </div>
                    <form className="inline-form" onSubmit={handlePaymentSubmit}>
                      <input min="0" placeholder="Amount received" step="0.01" type="number" value={paymentForm.amount} onChange={(event) => setPaymentForm({ ...paymentForm, amount: event.target.value })} />
                      <select value={paymentForm.paymentMethod} onChange={(event) => setPaymentForm({ ...paymentForm, paymentMethod: event.target.value })}>
                        <option value="CASH">Cash</option><option value="UPI">UPI</option><option value="CARD">Card</option>
                      </select>
                      <input placeholder="Note" value={paymentForm.note} onChange={(event) => setPaymentForm({ ...paymentForm, note: event.target.value })} />
                      <button className="primary-button" disabled={submitting} type="submit">Record payment</button>
                    </form>
                    <div className="split-section">
                      <div><h3>Sales</h3><ul className="stack-list">{ledger.sales.length === 0 ? <li>No sales yet.</li> : ledger.sales.map((sale) => <li key={sale.id}><strong>{sale.billNumber}</strong><span>{sale.saleDate}</span><span>Rs {Number(sale.grandTotal).toFixed(2)}</span><span>{sale.paymentStatus}</span></li>)}</ul></div>
                      <div><h3>Payments</h3><ul className="stack-list">{ledger.payments.length === 0 ? <li>No payments yet.</li> : ledger.payments.map((payment) => <li key={payment.id}><strong>Rs {Number(payment.amount).toFixed(2)}</strong><span>{payment.paymentDate}</span><span>{payment.paymentMethod}</span></li>)}</ul></div>
                    </div>
                  </>
                )}
              </section>
            </section>
          )}

          {activeTab === "billing" && (
            <section className="workspace-grid">
              <section className="panel form-panel">
                <div className="section-heading"><div><p className="eyebrow">Counter billing</p><h2>Create new bill</h2></div></div>
                <form className="form-grid" onSubmit={handleSaleSubmit}>
                  <label>
                    Customer for due sales
                    <select value={saleForm.customerId} onChange={(event) => setSaleForm({ ...saleForm, customerId: event.target.value })}>
                      <option value="">Walk-in customer</option>
                      {customers.map((customer) => <option key={customer.id} value={customer.id}>{customer.name} - Due Rs {Number(customer.currentDue).toFixed(2)}</option>)}
                    </select>
                  </label>
                  {saleForm.items.map((item, index) => (
                    <div className="line-item" key={`${item.productId}-${index}`}>
                      <select value={item.productId} onChange={(event) => updateSaleLine(index, "productId", event.target.value)}>
                        <option value="">Select product</option>
                        {products.map((product) => <option key={product.id} value={product.id}>{product.name} ({product.batchNumber}) - Stock {product.stockQuantity}</option>)}
                      </select>
                      <input min="1" type="number" value={item.quantity} onChange={(event) => updateSaleLine(index, "quantity", event.target.value)} />
                      {saleForm.items.length > 1 && <button className="ghost-button" onClick={() => removeSaleLine(index)} type="button">Remove</button>}
                    </div>
                  ))}
                  <button className="ghost-button" onClick={addSaleLine} type="button">Add product line</button>
                  <label>Discount<input min="0" step="0.01" type="number" value={saleForm.discount} onChange={(event) => setSaleForm({ ...saleForm, discount: event.target.value })} /></label>
                  <label>Paid amount<input min="0" step="0.01" type="number" value={saleForm.paidAmount} onChange={(event) => setSaleForm({ ...saleForm, paidAmount: event.target.value })} /></label>
                  <label>Payment method<select value={saleForm.paymentMethod} onChange={(event) => setSaleForm({ ...saleForm, paymentMethod: event.target.value })}><option value="CASH">Cash</option><option value="UPI">UPI</option><option value="CARD">Card</option><option value="CREDIT">Credit</option></select></label>
                  <label>Note<input value={saleForm.note} onChange={(event) => setSaleForm({ ...saleForm, note: event.target.value })} /></label>
                  <div className="bill-preview"><span>Subtotal: Rs {salePreview.subtotal.toFixed(2)}</span><span>Total: Rs {salePreview.total.toFixed(2)}</span><span className={salePreview.due > 0 ? "text-alert" : ""}>Due: Rs {salePreview.due.toFixed(2)}</span></div>
                  <button className="primary-button stretch" disabled={submitting} type="submit">Generate bill</button>
                </form>
              </section>
              <section className="panel">
                <div className="section-heading">
                  <div><p className="eyebrow">Printable invoice</p><h2>Latest generated bill</h2></div>
                  {latestSale && <button className="ghost-button" onClick={() => window.print()} type="button">Print bill</button>}
                </div>
                {latestSale ? (
                  <article className="receipt-card">
                    <h3>{latestSale.billNumber}</h3><p>{latestSale.customerName}</p><p>{latestSale.saleDate}</p>
                    <div className="receipt-items">{latestSale.items.map((item) => <div key={item.id || `${item.productId}-${item.productName}`} className="receipt-row"><span>{item.productName} x {item.quantity}</span><span>Rs {Number(item.lineTotal).toFixed(2)}</span></div>)}</div>
                    <div className="receipt-row"><strong>Total</strong><strong>Rs {Number(latestSale.grandTotal).toFixed(2)}</strong></div>
                    <div className="receipt-row"><span>Paid</span><span>Rs {Number(latestSale.paidAmount).toFixed(2)}</span></div>
                    <div className="receipt-row"><span>Due</span><span>Rs {Number(latestSale.dueAmount).toFixed(2)}</span></div>
                  </article>
                ) : <p className="muted-copy">Create a bill to preview and print it here.</p>}
                <div className="table-wrap"><table><thead><tr><th>Bill</th><th>Customer</th><th>Total</th><th>Status</th></tr></thead><tbody>
                  {sales.map((sale) => <tr key={sale.id}><td>{sale.billNumber}</td><td>{sale.customerName}</td><td>Rs {Number(sale.grandTotal).toFixed(2)}</td><td>{sale.paymentStatus}</td></tr>)}
                </tbody></table></div>
              </section>
            </section>
          )}

          {activeTab === "reports" && (
            <section className="workspace-grid">
              <section className="panel">
                <div className="section-heading"><div><p className="eyebrow">Reports</p><h2>Sales report</h2></div></div>
                <form className="inline-form" onSubmit={loadSalesReportWithRange}>
                  <input name="from" required type="date" />
                  <input name="to" required type="date" />
                  <button className="primary-button" type="submit">Load report</button>
                </form>
                <div className="table-wrap"><table><thead><tr><th>Bill</th><th>Date</th><th>Customer</th><th>Total</th><th>Due</th></tr></thead><tbody>
                  {reports.sales.map((sale) => <tr key={sale.id}><td>{sale.billNumber}</td><td>{sale.saleDate}</td><td>{sale.customerName}</td><td>Rs {Number(sale.grandTotal).toFixed(2)}</td><td>Rs {Number(sale.dueAmount).toFixed(2)}</td></tr>)}
                </tbody></table></div>
              </section>
              <section className="panel">
                <div className="section-heading"><div><p className="eyebrow">Dues</p><h2>Pending customer balances</h2></div></div>
                <ul className="stack-list">{reports.dues.map((customer) => <li key={customer.id}><strong>{customer.name}</strong><span>{customer.phone}</span><span>Rs {Number(customer.currentDue).toFixed(2)}</span></li>)}</ul>
              </section>
            </section>
          )}
        </>
      )}
    </main>
  );
}

function formatLabel(value) {
  return value.replace(/([A-Z])/g, " $1").replace(/^./, (letter) => letter.toUpperCase());
}

function isNumericField(name) {
  return ["purchasePrice", "salePrice", "stockQuantity", "lowStockThreshold"].includes(name);
}

function isPriceField(name) {
  return ["purchasePrice", "salePrice"].includes(name);
}

export default HomePage;
