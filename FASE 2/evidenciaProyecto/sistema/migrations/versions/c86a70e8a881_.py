"""empty message

Revision ID: c86a70e8a881
Revises: 
Create Date: 2024-11-09 12:37:53.556274

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = 'c86a70e8a881'
down_revision = None
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('categoria',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('name', sa.String(length=50), nullable=False),
    sa.PrimaryKeyConstraint('id'),
    sa.UniqueConstraint('name')
    )
    op.create_table('usuario',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('username', sa.String(length=50), nullable=False),
    sa.Column('password', sa.String(length=100), nullable=False),
    sa.Column('email', sa.String(length=120), nullable=False),
    sa.Column('is_admin', sa.Boolean(), nullable=True),
    sa.Column('reset_code', sa.String(length=6), nullable=True),
    sa.Column('reset_code_expiration', sa.DateTime(), nullable=True),
    sa.PrimaryKeyConstraint('id'),
    sa.UniqueConstraint('username')
    )
    op.create_table('carrito',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('usuario_id', sa.Integer(), nullable=False),
    sa.ForeignKeyConstraint(['usuario_id'], ['usuario.id'], ),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_table('producto',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('name', sa.String(length=155), nullable=False),
    sa.Column('price', sa.Integer(), nullable=False),
    sa.Column('marca', sa.String(length=100), nullable=False),
    sa.Column('descripcion', sa.String(length=1000), nullable=False),
    sa.Column('stock', sa.Integer(), nullable=False),
    sa.Column('category_id', sa.Integer(), nullable=False),
    sa.Column('en_oferta', sa.Boolean(), nullable=True),
    sa.ForeignKeyConstraint(['category_id'], ['categoria.id'], ),
    sa.PrimaryKeyConstraint('id'),
    sa.UniqueConstraint('name')
    )
    op.create_table('imagen',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('image_url', sa.String(length=255), nullable=False),
    sa.Column('producto_id', sa.Integer(), nullable=False),
    sa.ForeignKeyConstraint(['producto_id'], ['producto.id'], ),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_table('item_carrito',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('carrito_id', sa.Integer(), nullable=False),
    sa.Column('producto_id', sa.Integer(), nullable=False),
    sa.Column('cantidad', sa.Integer(), nullable=False),
    sa.ForeignKeyConstraint(['carrito_id'], ['carrito.id'], ),
    sa.ForeignKeyConstraint(['producto_id'], ['producto.id'], ),
    sa.PrimaryKeyConstraint('id')
    )
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_table('item_carrito')
    op.drop_table('imagen')
    op.drop_table('producto')
    op.drop_table('carrito')
    op.drop_table('usuario')
    op.drop_table('categoria')
    # ### end Alembic commands ###
